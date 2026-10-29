import React, { useEffect, useState } from 'react';
import { claimsApi } from '../api/client';
import { Claim, ClaimStatus } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { ShieldAlert, CheckCircle, XCircle, Lock } from 'lucide-react';

export const ModerationPage: React.FC = () => {
  const [claims, setClaims] = useState<Claim[]>([]);
  const [selectedStatus, setSelectedStatus] = useState<ClaimStatus | ''>('PENDING');
  const [isLoading, setIsLoading] = useState(true);

  const loadClaims = async () => {
    setIsLoading(true);
    try {
      const data = await claimsApi.getModerationClaims(selectedStatus || undefined, 0, 50);
      setClaims(data.content);
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadClaims();
  }, [selectedStatus]);

  const handleReview = async (claimId: string, status: ClaimStatus) => {
    const note = window.prompt(`Enter moderation decision note for ${status}:`, status === 'APPROVED' ? 'Proof of ownership validated' : 'Rejected after inspection');
    if (note === null) return;

    try {
      await claimsApi.review(claimId, { status, reviewNotes: note });
      loadClaims();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to update claim');
    }
  };

  return (
    <div className="space-y-6 pb-12">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
            <ShieldAlert className="w-6 h-6 text-amber-500" />
            Moderator Claims Review
          </h1>
          <p className="text-xs text-slate-500 mt-1">
            Evaluate ownership proof, cross-examine confidential serial numbers, and resolve item recovery disputes
          </p>
        </div>

        {/* Status Filter */}
        <div className="flex items-center gap-2 text-xs">
          <span className="text-slate-500 font-medium">Filter:</span>
          {(['PENDING', 'APPROVED', 'REJECTED', ''] as const).map((s) => (
            <button
              key={s}
              onClick={() => setSelectedStatus(s)}
              className={`px-3 py-1 rounded-md font-medium transition-colors ${
                selectedStatus === s
                  ? 'bg-navy-900 text-white'
                  : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50'
              }`}
            >
              {s || 'All Claims'}
            </button>
          ))}
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Loading moderation queue...</p>
        </div>
      ) : claims.length === 0 ? (
        <EmptyState
          icon={ShieldAlert}
          title="Queue Empty"
          description={`No ${selectedStatus ? selectedStatus.toLowerCase() : ''} claims requiring review at this time.`}
        />
      ) : (
        <div className="space-y-4">
          {claims.map((claim) => (
            <Card key={claim.id} className="p-5">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 pb-3 border-b border-slate-100">
                <div>
                  <div className="flex items-center gap-2">
                    <Badge status={claim.status} />
                    <span className="text-xs text-slate-400">
                      Filed: {new Date(claim.createdAt).toLocaleString()}
                    </span>
                  </div>
                  <h3 className="text-base font-bold text-slate-900 mt-1">
                    Claimant: {claim.claimantName} &rarr; Item: {claim.reportTitle}
                  </h3>
                </div>

                {claim.status === 'PENDING' && (
                  <div className="flex items-center gap-2">
                    <Button
                      size="sm"
                      variant="danger"
                      onClick={() => handleReview(claim.id, 'REJECTED')}
                    >
                      <XCircle className="w-4 h-4 mr-1" />
                      Reject
                    </Button>
                    <Button
                      size="sm"
                      className="bg-emerald-600 hover:bg-emerald-700 text-white"
                      onClick={() => handleReview(claim.id, 'APPROVED')}
                    >
                      <CheckCircle className="w-4 h-4 mr-1" />
                      Approve & Handover
                    </Button>
                  </div>
                )}
              </div>

              <div className="mt-3 space-y-3 text-xs">
                <div>
                  <span className="font-semibold text-slate-700">Claimant Statement:</span>
                  <p className="mt-1 text-slate-600 p-2.5 rounded bg-slate-50 border border-slate-200">
                    {claim.claimDescription}
                  </p>
                </div>

                {claim.privateVerificationEvidence && (
                  <div className="p-3 bg-amber-50/70 border border-amber-200 rounded text-slate-800 space-y-1">
                    <div className="flex items-center gap-1 font-semibold text-amber-900">
                      <Lock className="w-3.5 h-3.5 text-amber-600" />
                      <span>Confidential Evidence:</span>
                    </div>
                    {claim.privateVerificationEvidence.serialNumber && (
                      <div>Serial / Unique ID: <strong>{claim.privateVerificationEvidence.serialNumber}</strong></div>
                    )}
                    {claim.privateVerificationEvidence.identifyingMarks && (
                      <div>Identifying Marks: <strong>{claim.privateVerificationEvidence.identifyingMarks}</strong></div>
                    )}
                    {claim.privateVerificationEvidence.additionalSecretDetails && (
                      <div>Secret Details: <strong>{claim.privateVerificationEvidence.additionalSecretDetails}</strong></div>
                    )}
                  </div>
                )}

                {claim.reviewNotes && (
                  <div className="text-slate-500 italic">
                    Resolution note: {claim.reviewNotes}
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
