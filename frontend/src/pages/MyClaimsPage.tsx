import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { claimsApi } from '../api/client';
import { Claim } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { ShieldCheck, Calendar, ArrowRight, CheckCircle2, AlertCircle } from 'lucide-react';

export const MyClaimsPage: React.FC = () => {
  const [claims, setClaims] = useState<Claim[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    claimsApi.getMyClaims(0, 50)
      .then((data) => setClaims(data.content))
      .catch((err) => console.error(err))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <div className="space-y-6 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-navy-900">My Recovery Claims</h1>
        <p className="text-xs text-slate-500 mt-1">
          Review the status of ownership claims you have submitted for found items
        </p>
      </div>

      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Loading your claims...</p>
        </div>
      ) : claims.length === 0 ? (
        <EmptyState
          title="No Claims Submitted"
          description="You haven't filed any ownership claims yet. Browse found items or check your matches to claim a misplaced item."
          actionText="Browse Found Items"
          onAction={() => window.location.href = '/browse?type=FOUND'}
        />
      ) : (
        <div className="space-y-4">
          {claims.map((claim) => (
            <Card key={claim.id} className="p-4 sm:p-5">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 pb-3 border-b border-slate-100">
                <div>
                  <div className="flex items-center gap-2">
                    <Badge status={claim.status} />
                    <span className="text-xs text-slate-400 flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      Submitted {new Date(claim.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                  <h3 className="text-base font-semibold text-slate-900 mt-1">
                    Claim for: {claim.reportTitle}
                  </h3>
                </div>

                <Link to={`/reports/${claim.reportId}`}>
                  <Button size="sm" variant="outline">
                    View Item <ArrowRight className="w-3.5 h-3.5 ml-1" />
                  </Button>
                </Link>
              </div>

              <div className="pt-3 text-xs text-slate-600 space-y-2">
                <div>
                  <span className="font-semibold text-slate-700">Your claim note: </span>
                  <span>{claim.claimDescription}</span>
                </div>

                {claim.reviewNotes && (
                  <div className={`p-2.5 rounded-md border ${claim.status === 'APPROVED' ? 'bg-emerald-50 border-emerald-200 text-emerald-800' : 'bg-slate-50 border-slate-200 text-slate-700'}`}>
                    <span className="font-semibold">Reviewer Response: </span>
                    <span>{claim.reviewNotes}</span>
                  </div>
                )}
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
