import React from 'react';
import { ReportStatus, ReportType, ClaimStatus, AccountStatus } from '../../types';

interface BadgeProps {
  type?: ReportType;
  status?: ReportStatus | ClaimStatus | AccountStatus;
  children?: React.ReactNode;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({ type, status, children, className = '' }) => {
  if (type) {
    if (type === 'LOST') {
      return (
        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-100 text-rose-700 ${className}`}>
          LOST
        </span>
      );
    }
    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-100 text-emerald-800 ${className}`}>
        FOUND
      </span>
    );
  }

  if (status) {
    const styles: Record<string, string> = {
      ACTIVE: 'bg-blue-100 text-blue-800',
      MATCHED: 'bg-indigo-100 text-indigo-800',
      CLAIM_PENDING: 'bg-amber-100 text-amber-800',
      PENDING: 'bg-amber-100 text-amber-800',
      UNDER_REVIEW: 'bg-purple-100 text-purple-800',
      APPROVED: 'bg-emerald-100 text-emerald-800',
      RETURNED: 'bg-emerald-100 text-emerald-800',
      COMPLETED: 'bg-emerald-100 text-emerald-800',
      REJECTED: 'bg-red-100 text-red-800',
      WITHDRAWN: 'bg-slate-100 text-slate-700',
      CLOSED: 'bg-slate-200 text-slate-700',
      SUSPENDED: 'bg-rose-100 text-rose-800',
    };

    const label = status.replace('_', ' ');

    return (
      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${styles[status] || 'bg-slate-100 text-slate-700'} ${className}`}>
        {label}
      </span>
    );
  }

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700 ${className}`}>
      {children}
    </span>
  );
};
