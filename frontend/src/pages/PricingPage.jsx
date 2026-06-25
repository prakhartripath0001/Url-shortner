import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { Check, Crown, Zap, Loader2, Star } from "lucide-react";
import toast from "react-hot-toast";
import { paymentService, loadRazorpay, openRazorpayCheckout } from "../../services/paymentService";
import { useAuthStore } from "../../store/authStore";
import DashboardLayout from "../../components/DashboardLayout";

const PLANS = [
  {
    id: "free",
    name: "Free",
    price: 0,
    currency: "₹",
    period: "forever",
    description: "Perfect to get started",
    color: "slate",
    features: [
      "10 links per month",
      "Basic analytics (7 days)",
      "Standard short codes",
      "QR code generation",
      "Link expiry",
    ],
    cta: "Current Plan",
    disabled: true,
  },
  {
    id: "pro",
    name: "Pro",
    price: 299,
    currency: "₹",
    period: "month",
    description: "For creators & marketers",
    color: "indigo",
    popular: true,
    features: [
      "Unlimited links",
      "Advanced analytics (1 year)",
      "Custom aliases",
      "Private links",
      "Password protection",
      "Bulk URL shortening",
      "API access",
      "Priority support",
    ],
    cta: "Upgrade to Pro",
    disabled: false,
  },
  {
    id: "business",
    name: "Business",
    price: 999,
    currency: "₹",
    period: "month",
    description: "For teams & agencies",
    color: "violet",
    features: [
      "Everything in Pro",
      "Team collaboration (5 seats)",
      "White-label links",
      "Custom domains",
      "Webhook integrations",
      "SSO / SAML",
      "Dedicated account manager",
      "SLA 99.99% uptime",
    ],
    cta: "Upgrade to Business",
    disabled: false,
  },
];

export default function PricingPage() {
  const user = useAuthStore((s) => s.user);
  const [selectedPlan, setSelectedPlan] = useState(null);

  const payMutation = useMutation({
    mutationFn: async (plan) => {
      // 1. Load Razorpay script
      const loaded = await loadRazorpay();
      if (!loaded) throw new Error("Failed to load payment gateway. Please check your connection.");

      // 2. Create order on our backend
      const { data: order } = await paymentService.createOrder(plan.id);

      // 3. Open Razorpay modal
      return new Promise((resolve, reject) => {
        openRazorpayCheckout(
          order,
          user,
          async (paymentResponse) => {
            try {
              // 4. Verify payment signature on backend
              await paymentService.verifyPayment({
                razorpayOrderId: paymentResponse.razorpay_order_id,
                razorpayPaymentId: paymentResponse.razorpay_payment_id,
                razorpaySignature: paymentResponse.razorpay_signature,
                plan: plan.id,
              });
              resolve(paymentResponse);
            } catch (e) {
              reject(e);
            }
          },
          (reason) => reject(new Error(reason))
        );
      });
    },
    onSuccess: (_, plan) => {
      toast.success(`🎉 Welcome to ${plan.name}! Your account has been upgraded.`);
    },
    onError: (err) => {
      toast.error(err.message || "Payment failed. Please try again.");
    },
  });

  const handleUpgrade = async (plan) => {
    if (plan.disabled) return;
    setSelectedPlan(plan.id);
    try {
      await payMutation.mutateAsync(plan);
    } finally {
      setSelectedPlan(null);
    }
  };

  return (
    <DashboardLayout>
      {/* Header */}
      <div className="text-center mb-12">
        <div className="inline-flex items-center gap-2 bg-indigo-500/20 border border-indigo-500/20 rounded-full px-4 py-1.5 text-indigo-400 text-sm font-medium mb-4">
          <Crown size={14} /> Simple, transparent pricing
        </div>
        <h1 className="text-3xl font-bold text-white mb-3">
          Choose the plan that fits your needs
        </h1>
        <p className="text-slate-400 max-w-lg mx-auto">
          Start free and upgrade anytime. All paid plans include a 7-day money-back guarantee.
        </p>
      </div>

      {/* Plans */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl mx-auto">
        {PLANS.map((plan) => (
          <div
            key={plan.id}
            className={`relative rounded-2xl border p-7 flex flex-col transition-all duration-200
              ${plan.popular
                ? "bg-gradient-to-b from-indigo-600/20 to-indigo-900/20 border-indigo-500/40 shadow-xl shadow-indigo-500/10 scale-105"
                : "bg-white/5 border-white/10"
              }`}
          >
            {plan.popular && (
              <div className="absolute -top-3 left-1/2 -translate-x-1/2">
                <div className="flex items-center gap-1.5 bg-gradient-to-r from-indigo-500 to-violet-500 text-white text-xs font-bold rounded-full px-4 py-1.5 shadow-lg">
                  <Star size={12} fill="currentColor" /> Most Popular
                </div>
              </div>
            )}

            {/* Plan header */}
            <div className="mb-6">
              <div className={`w-10 h-10 rounded-xl mb-4 flex items-center justify-center
                ${plan.id === "free" ? "bg-slate-700" :
                  plan.id === "pro" ? "bg-indigo-500/30" : "bg-violet-500/30"}`}
              >
                {plan.id === "free" ? <Zap size={20} className="text-slate-300" /> :
                  plan.id === "pro" ? <Crown size={20} className="text-indigo-300" /> :
                    <Star size={20} className="text-violet-300" />}
              </div>
              <h3 className="text-xl font-bold text-white">{plan.name}</h3>
              <p className="text-slate-400 text-sm mt-1">{plan.description}</p>
            </div>

            {/* Price */}
            <div className="mb-6">
              <div className="flex items-end gap-1">
                <span className="text-3xl font-bold text-white">{plan.currency}{plan.price}</span>
                <span className="text-slate-400 text-sm mb-1">/{plan.period}</span>
              </div>
              {plan.price > 0 && (
                <p className="text-xs text-slate-500 mt-1">Billed monthly • Cancel anytime</p>
              )}
            </div>

            {/* Features */}
            <ul className="space-y-3 flex-1 mb-8">
              {plan.features.map((f) => (
                <li key={f} className="flex items-start gap-2.5">
                  <Check size={15} className={`flex-shrink-0 mt-0.5 ${plan.popular ? "text-indigo-400" : "text-emerald-400"}`} />
                  <span className="text-sm text-slate-300">{f}</span>
                </li>
              ))}
            </ul>

            {/* CTA */}
            <button
              id={`upgrade-${plan.id}`}
              onClick={() => handleUpgrade(plan)}
              disabled={plan.disabled || (payMutation.isPending && selectedPlan === plan.id)}
              className={`w-full py-3 rounded-xl text-sm font-semibold transition-all
                ${plan.disabled
                  ? "bg-white/5 border border-white/10 text-slate-500 cursor-not-allowed"
                  : plan.popular
                    ? "bg-gradient-to-r from-indigo-600 to-violet-600 hover:from-indigo-500 hover:to-violet-500 text-white shadow-lg shadow-indigo-500/25"
                    : "bg-white/10 hover:bg-white/15 text-white border border-white/10"
                } disabled:opacity-60 disabled:cursor-not-allowed`}
            >
              {payMutation.isPending && selectedPlan === plan.id
                ? <span className="flex items-center justify-center gap-2"><Loader2 size={16} className="animate-spin" /> Processing...</span>
                : plan.cta
              }
            </button>
          </div>
        ))}
      </div>

      {/* FAQ / Trust */}
      <div className="mt-16 text-center">
        <p className="text-slate-400 text-sm">
          🔒 Payments secured by{" "}
          <span className="text-indigo-400 font-medium">Razorpay</span> •
          PCI DSS Level 1 Certified
        </p>
        <p className="text-slate-500 text-xs mt-2">
          Need enterprise pricing? <a href="mailto:sales@shortify.com" className="text-indigo-400 hover:underline">Contact us</a>
        </p>
      </div>
    </DashboardLayout>
  );
}
