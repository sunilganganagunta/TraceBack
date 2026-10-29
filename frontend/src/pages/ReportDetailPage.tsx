import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { reportsApi, matchesApi, claimsApi } from '../api/client';
import { ItemReport, Match, Claim, ClaimStatus } from '../types';
import { useAuth } from '../context/AuthContext';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { ScoreGauge } from '../components/ui/ScoreGauge';
import { ClaimModal } from '../components/ui/ClaimModal';
import {
  MapPin,
  Calendar,
  Tag,
  ShieldCheck,
  Sparkles,
  ArrowLeft,
  Lock,
  CheckCircle,
  XCircle,
  AlertCircle,
} from 'lucide-react';

export const ReportDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user, isAuthenticated, isModerator } = useAuth();

  const [report, setReport] = useState<ItemReport | null>(null);
  const [matches, setMatches] = useState<Match[]>([]);
  const [claims, setClaims] = useState<Claim[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isClaimModalOpen, setIsClaimModalOpen] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const loadData = async () => {
    if (!id) return;
    setIsLoading(true);
    try {
      const data = await reportsApi.getById(id);
      setReport(data);

      // If user is report owner or moderator, load matches and claims
      if (data.owner || isModerator) {
        matchesApi.getForReport(id)
          .then(setMatches)
          .catch(() => {});

        claimsApi.getForReport(id)
          .then(setClaims)
          .catch(() => {});
      }
    } catch (err) {
      console.error('Failed to load report', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [id, isModerator]);

  const handleReviewClaim = async (claimId: string, status: ClaimStatus) => {
    const reason = window.prompt(`Enter review notes for ${status}:`, status === 'APPROVED' ? 'Evidence verified successfully' : 'Insufficient verification details');
    if (reason === null) return;

    try {
      await claimsApi.review(claimId, { status, reviewNotes: reason });
      setSuccessMessage(`Claim successfully marked as ${status}`);
      loadData();
    } catch (err: any) {
      alert(err.response?.data?.message || 'Failed to update claim');
    }
  };

  const handleWithdrawReport = async () => {
    if (!id || !window.confirm('Are you sure you want to withdraw this report?')) return;
    try {
      await reportsApi.withdraw(id);
      navigate('/my-reports');
    } catch (err) {
      alert('Failed to withdraw report');
    }
  };

  if (isLoading) {
    return (
      <div className="text-center py-24">
        <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
        <p className="mt-3 text-xs text-slate-500">Loading item report details...</p>
      </div>
    );
  }

  if (!report) {
    return (
      <div className="text-center py-16">
        <h2 className="text-lg font-bold text-slate-900">Report Not Found</h2>
        <p className="text-xs text-slate-500 mt-1">This report may have been withdrawn or removed.</p>
        <Link to="/browse" className="mt-4 inline-block">
          <Button size="sm" variant="outline">Back to Browse</Button>
        </Link>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-4xl mx-auto pb-12">
      {/* Back button */}
      <div>
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-1.5 text-xs font-medium text-slate-600 hover:text-navy-900"
        >
          <ArrowLeft className="w-4 h-4" /> Back
        </button>
      </div>

      {successMessage && (
        <div className="p-3 bg-emerald-50 border border-emerald-200 text-emerald-800 rounded-lg text-xs flex items-center justify-between">
          <span>{successMessage}</span>
          <button onClick={() => setSuccessMessage(null)} className="text-emerald-600 hover:text-emerald-900 font-bold">×</button>
        </div>
      )}

      {/* Main Item Card */}
      <Card>
        <div className="flex flex-col sm:flex-row justify-between items-start gap-4 pb-4 border-b border-slate-100">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Badge type={report.reportType} />
              <Badge status={report.status} />
              <span className="text-xs text-slate-400">
                Reported by {report.owner ? 'You' : report.ownerName}
              </span>
            </div>
            <h1 className="text-xl sm:text-2xl font-bold text-navy-900">
              {report.title}
            </h1>
          </div>

          {/* Action on Item */}
          <div className="flex items-center gap-2">
            {!report.owner && report.reportType === 'FOUND' && report.status !== 'RETURNED' && report.status !== 'CLOSED' && (
              <Button
                size="sm"
                onClick={() => {
                  if (!isAuthenticated) {
                    navigate('/login');
                  } else {
                    setIsClaimModalOpen(true);
                  }
                }}
              >
                <ShieldCheck className="w-4 h-4 mr-1" />
                Claim This Item
              </Button>
            )}

            {report.owner && report.status !== 'WITHDRAWN' && (
              <Button size="sm" variant="danger" onClick={handleWithdrawReport}>
                Withdraw Report
              </Button>
            )}
          </div>
        </div>

        {/* Details Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 py-4 text-xs text-slate-600 border-b border-slate-100">
          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Tag className="w-4 h-4 text-slate-400" />
              <span>Category: <strong className="text-slate-800">{report.categoryName}</strong></span>
            </div>
            {report.brand && (
              <div className="flex items-center gap-2">
                <span className="w-4 text-center text-slate-400 font-bold">•</span>
                <span>Brand: <strong className="text-slate-800">{report.brand}</strong></span>
              </div>
            )}
            <div className="flex items-center gap-2">
              <span className="w-4 text-center text-slate-400 font-bold">•</span>
              <span>Color: <strong className="text-slate-800">{report.color}</strong></span>
            </div>
          </div>

          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-slate-400" />
              <span>Date {report.reportType === 'LOST' ? 'Lost' : 'Found'}: <strong className="text-slate-800">{new Date(report.dateLostOrFound).toLocaleDateString()}</strong></span>
            </div>
            <div className="flex items-start gap-2">
              <MapPin className="w-4 h-4 text-slate-400 flex-shrink-0 mt-0.5" />
              <div>
                <span>Location: <strong className="text-slate-800">{report.publicLocationLabel}</strong></span>
                <span className="block text-[11px] text-slate-400 mt-0.5">
                  Approx coords: [{report.approximateLongitude}, {report.approximateLatitude}]
                </span>
              </div>
            </div>
          </div>
        </div>

        {/* Description & Distinguishing Features */}
        <div className="pt-4 space-y-3">
          <div>
            <h4 className="text-xs font-semibold text-slate-900 uppercase tracking-wider mb-1">
              Description
            </h4>
            <p className="text-xs text-slate-700 whitespace-pre-wrap leading-relaxed">
              {report.description}
            </p>
          </div>

          {report.distinguishingFeatures && (
            <div className="p-3 bg-slate-50 rounded-md border border-slate-200">
              <h4 className="text-xs font-semibold text-slate-900 mb-1">
                Distinguishing Marks / Features
              </h4>
              <p className="text-xs text-slate-600">
                {report.distinguishingFeatures}
              </p>
            </div>
          )}
        </div>
      </Card>

      {/* Owner View: Intelligent Matches Section */}
      {report.owner && (
        <Card title="Potential Matches Discovered by Matching Engine" subtitle="Computed via multi-signal similarity scoring">
          {matches.length === 0 ? (
            <div className="text-center py-6 text-xs text-slate-500">
              No matching reports detected yet. As new reports are filed, TraceBack's algorithm will automatically evaluate them.
            </div>
          ) : (
            <div className="space-y-4">
              {matches.map((match) => (
                <div key={match.id} className="p-4 border border-slate-200 rounded-lg bg-white space-y-3">
                  <div className="flex justify-between items-start">
                    <div>
                      <h4 className="text-sm font-semibold text-slate-900">
                        Opposing Report: {match.lostReportId === report.id ? match.foundReport?.title : match.lostReport?.title}
                      </h4>
                      <p className="text-xs text-slate-500 mt-0.5">
                        Location: {match.lostReportId === report.id ? match.foundReport?.locationLabel : match.lostReport?.locationLabel}
                      </p>
                    </div>
                    <Link to={`/reports/${match.lostReportId === report.id ? match.foundReportId : match.lostReportId}`}>
                      <Button size="sm" variant="outline">
                        Inspect Report
                      </Button>
                    </Link>
                  </div>

                  <ScoreGauge
                    score={match.score}
                    reasons={match.reasons}
                    breakdown={match.scoreBreakdown}
                  />
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Owner / Mod View: Submitted Ownership Claims */}
      {(report.owner || isModerator) && (
        <Card title="Submitted Recovery Claims" subtitle="Claims filed by community members for this item">
          {claims.length === 0 ? (
            <div className="text-center py-6 text-xs text-slate-500">
              No ownership claims filed yet for this item.
            </div>
          ) : (
            <div className="space-y-4">
              {claims.map((claim) => (
                <div key={claim.id} className="p-4 border border-slate-200 rounded-lg bg-slate-50 space-y-3">
                  <div className="flex justify-between items-center">
                    <div>
                      <span className="font-semibold text-slate-900 text-xs">
                        Claim by: {claim.claimantName}
                      </span>
                      <span className="text-[11px] text-slate-400 ml-2">
                        {new Date(claim.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                    <Badge status={claim.status} />
                  </div>

                  <p className="text-xs text-slate-700 bg-white p-2.5 rounded border border-slate-200">
                    {claim.claimDescription}
                  </p>

                  {/* Private Evidence Section */}
                  {claim.privateVerificationEvidence && (
                    <div className="p-3 bg-amber-50/60 border border-amber-200 rounded text-xs space-y-1">
                      <div className="flex items-center gap-1.5 font-semibold text-amber-900">
                        <Lock className="w-3.5 h-3.5" />
                        <span>Confidential Verification Evidence:</span>
                      </div>
                      {claim.privateVerificationEvidence.serialNumber && (
                        <div>Serial/Identifier: <strong>{claim.privateVerificationEvidence.serialNumber}</strong></div>
                      )}
                      {claim.privateVerificationEvidence.identifyingMarks && (
                        <div>Unique Marks: <strong>{claim.privateVerificationEvidence.identifyingMarks}</strong></div>
                      )}
                      {claim.privateVerificationEvidence.additionalSecretDetails && (
                        <div>Secret Details: <strong>{claim.privateVerificationEvidence.additionalSecretDetails}</strong></div>
                      )}
                    </div>
                  )}

                  {/* Actions for Owner / Moderator */}
                  {claim.status === 'PENDING' && (
                    <div className="flex items-center justify-end gap-2 pt-2">
                      <Button
                        size="sm"
                        variant="danger"
                        onClick={() => handleReviewClaim(claim.id, 'REJECTED')}
                      >
                        <XCircle className="w-4 h-4 mr-1" />
                        Reject Claim
                      </Button>
                      <Button
                        size="sm"
                        className="bg-emerald-600 hover:bg-emerald-700 text-white"
                        onClick={() => handleReviewClaim(claim.id, 'APPROVED')}
                      >
                        <CheckCircle className="w-4 h-4 mr-1" />
                        Verify & Approve Claim
                      </Button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </Card>
      )}

      {/* Claim Modal */}
      <ClaimModal
        reportId={report.id}
        reportTitle={report.title}
        isOpen={isClaimModalOpen}
        onClose={() => setIsClaimModalOpen(false)}
        onSuccess={() => {
          setSuccessMessage('Your ownership claim was submitted successfully! The reporter has been notified.');
          loadData();
        }}
      />
    </div>
  );
};
