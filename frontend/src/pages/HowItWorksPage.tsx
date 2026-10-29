import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import {
  Sparkles,
  MapPin,
  ShieldCheck,
  Search,
  Bell,
  CheckCircle2,
  Lock,
  ArrowRight,
  FileCheck2,
} from 'lucide-react';

export const HowItWorksPage: React.FC = () => {
  return (
    <div className="space-y-12 max-w-4xl mx-auto pb-12">
      {/* Header */}
      <div className="text-center space-y-3">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-primary-50 text-primary-700 text-xs font-semibold border border-primary-200">
          <Sparkles className="w-3.5 h-3.5" />
          Transparent Recovery Workflow
        </div>
        <h1 className="text-3xl sm:text-4xl font-extrabold text-navy-900">
          How TraceBack Works
        </h1>
        <p className="text-sm text-slate-600 max-w-xl mx-auto">
          A step-by-step guide to reporting, matching, verifying, and recovering lost belongings securely.
        </p>
      </div>

      {/* 4 Steps */}
      <div className="space-y-6">
        {/* Step 1 */}
        <Card className="flex flex-col sm:flex-row items-start gap-5 p-6">
          <div className="w-12 h-12 rounded-xl bg-navy-900 text-white font-bold text-xl flex items-center justify-center flex-shrink-0 shadow-sm">
            1
          </div>
          <div className="space-y-2 flex-1">
            <h3 className="text-base font-bold text-navy-900">
              Submit a Lost or Found Report
            </h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              Whether you misplaced a laptop, wallet, keys, or student card, or found an item left behind, file a report with key details: item title, category taxonomy, color, brand, approximate date, and public location label.
            </p>
            <div className="flex items-center gap-2 pt-1 text-xs text-primary-700">
              <MapPin className="w-3.5 h-3.5" />
              <span>Location coordinates are stored via MongoDB GeoJSON while public views only display rounded approximate locations.</span>
            </div>
          </div>
        </Card>

        {/* Step 2 */}
        <Card className="flex flex-col sm:flex-row items-start gap-5 p-6">
          <div className="w-12 h-12 rounded-xl bg-primary-600 text-white font-bold text-xl flex items-center justify-center flex-shrink-0 shadow-sm">
            2
          </div>
          <div className="space-y-2 flex-1">
            <h3 className="text-base font-bold text-navy-900">
              Deterministic Multi-Signal Matching
            </h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              When a new report is published, TraceBack's matching engine automatically searches active opposing reports. It computes a transparent similarity score (0–100%) by evaluating:
            </p>
            <ul className="grid grid-cols-2 gap-2 text-xs text-slate-700 pt-1">
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Category taxonomy (30 pts)</li>
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Title keyword overlap (25 pts)</li>
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Description semantics (15 pts)</li>
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Geospatial proximity (15 pts)</li>
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Date compatibility (10 pts)</li>
              <li className="flex items-center gap-1.5"><CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" /> Color & brand attributes (10 pts)</li>
            </ul>
          </div>
        </Card>

        {/* Step 3 */}
        <Card className="flex flex-col sm:flex-row items-start gap-5 p-6">
          <div className="w-12 h-12 rounded-xl bg-amber-500 text-white font-bold text-xl flex items-center justify-center flex-shrink-0 shadow-sm">
            3
          </div>
          <div className="space-y-2 flex-1">
            <h3 className="text-base font-bold text-navy-900">
              Submit Ownership Claim with Confidential Evidence
            </h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              If you discover a matching found item, submit an ownership claim. To prevent fraudulent claims, you can provide confidential proof that is never revealed to the public:
            </p>
            <div className="p-3 bg-amber-50 rounded-lg border border-amber-200 text-xs text-amber-900 flex items-start gap-2">
              <Lock className="w-4 h-4 flex-shrink-0 mt-0.5" />
              <span>
                Provide hidden serial numbers, unique marks, lock-screen wallpaper descriptions, or purchase receipts. Only the reporter or assigned moderator can inspect this evidence.
              </span>
            </div>
          </div>
        </Card>

        {/* Step 4 */}
        <Card className="flex flex-col sm:flex-row items-start gap-5 p-6">
          <div className="w-12 h-12 rounded-xl bg-emerald-600 text-white font-bold text-xl flex items-center justify-center flex-shrink-0 shadow-sm">
            4
          </div>
          <div className="space-y-2 flex-1">
            <h3 className="text-base font-bold text-navy-900">
              Verification, Approval, and Handover
            </h3>
            <p className="text-xs text-slate-600 leading-relaxed">
              The reporter reviews the claim and evidence. Upon approval, the item is marked as <strong>RETURNED</strong>, real-time in-app notifications are dispatched, competing claims are settled, and handover can proceed safely at a verified campus or workplace location.
            </p>
          </div>
        </Card>
      </div>

      {/* CTA Box */}
      <div className="bg-navy-900 text-white rounded-xl p-8 text-center space-y-4 shadow-sm">
        <h2 className="text-2xl font-bold">Ready to recover your belongings?</h2>
        <p className="text-xs text-slate-300 max-w-md mx-auto">
          Start browsing community listings or file a report in under two minutes.
        </p>
        <div className="flex justify-center gap-3 pt-2">
          <Link to="/browse">
            <Button size="md" className="bg-primary-600 hover:bg-primary-700">
              <Search className="w-4 h-4 mr-1" /> Browse Items
            </Button>
          </Link>
          <Link to="/register">
            <Button size="md" variant="secondary">
              Get Started Free <ArrowRight className="w-4 h-4 ml-1" />
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};
