import React, { useState } from 'react';
import { ScoreBreakdown } from '../../types';
import { CheckCircle2, Info, ChevronDown, ChevronUp } from 'lucide-react';

interface ScoreGaugeProps {
  score: number;
  reasons: string[];
  breakdown?: ScoreBreakdown;
  className?: string;
}

export const ScoreGauge: React.FC<ScoreGaugeProps> = ({ score, reasons, breakdown, className = '' }) => {
  const [showDetails, setShowDetails] = useState(false);

  // Score coloring
  const getColor = (s: number) => {
    if (s >= 75) return 'text-emerald-700 bg-emerald-50 border-emerald-200';
    if (s >= 50) return 'text-primary-700 bg-primary-50 border-primary-200';
    return 'text-amber-700 bg-amber-50 border-amber-200';
  };

  const getProgressColor = (s: number) => {
    if (s >= 75) return 'bg-emerald-500';
    if (s >= 50) return 'bg-primary-500';
    return 'bg-amber-500';
  };

  // Limit initial reasons to max 3 as requested
  const visibleReasons = showDetails ? reasons : reasons.slice(0, 3);

  return (
    <div className={`rounded-lg border p-4 ${getColor(score)} ${className}`}>
      <div className="flex items-center justify-between gap-4">
        <div>
          <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">
            Compatibility Score
          </span>
          <div className="flex items-baseline gap-2 mt-0.5">
            <span className="text-2xl font-bold">{Math.round(score)}%</span>
            <span className="text-xs font-medium text-slate-500">similarity match</span>
          </div>
        </div>

        {/* Mini progress bar */}
        <div className="w-24 sm:w-32 bg-slate-200 h-2.5 rounded-full overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-500 ${getProgressColor(score)}`}
            style={{ width: `${Math.min(100, Math.max(0, score))}%` }}
          />
        </div>
      </div>

      {/* Matching Reasons */}
      {visibleReasons.length > 0 && (
        <ul className="mt-3 space-y-1.5 text-xs text-slate-700">
          {visibleReasons.map((reason, idx) => (
            <li key={idx} className="flex items-start gap-1.5">
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 flex-shrink-0 mt-0.5" />
              <span>{reason}</span>
            </li>
          ))}
        </ul>
      )}

      {/* Expand / Collapse Details Button */}
      {(reasons.length > 3 || breakdown) && (
        <button
          onClick={() => setShowDetails(!showDetails)}
          className="mt-2 text-xs font-medium text-primary-600 hover:text-primary-800 flex items-center gap-1 focus:outline-none"
        >
          {showDetails ? (
            <>
              Less details <ChevronUp className="w-3 h-3" />
            </>
          ) : (
            <>
              More details {reasons.length > 3 && `(${reasons.length - 3} more)`} <ChevronDown className="w-3 h-3" />
            </>
          )}
        </button>
      )}

      {/* Detailed breakdown sub-grid */}
      {showDetails && breakdown && (
        <div className="mt-3 pt-3 border-t border-slate-200/60 grid grid-cols-2 sm:grid-cols-3 gap-2 text-[11px] text-slate-600">
          <div>Category: <span className="font-semibold">{breakdown.categoryScore} pts</span></div>
          <div>Keywords: <span className="font-semibold">{Math.round(breakdown.titleScore)} pts</span></div>
          <div>Description: <span className="font-semibold">{Math.round(breakdown.descriptionScore)} pts</span></div>
          <div>Attributes: <span className="font-semibold">{breakdown.attributeScore} pts</span></div>
          <div>Date: <span className="font-semibold">{breakdown.dateScore} pts</span></div>
          <div>Proximity: <span className="font-semibold">{breakdown.proximityScore} pts</span></div>
        </div>
      )}

      {/* Mandatory Disclaimer from requirements */}
      <div className="mt-3 flex items-start gap-1.5 text-[11px] text-slate-500 border-t border-slate-200/60 pt-2">
        <Info className="w-3.5 h-3.5 flex-shrink-0 mt-0.5" />
        <span>
          Matching score indicates statistical attribute similarity and does not guarantee legal ownership.
        </span>
      </div>
    </div>
  );
};
