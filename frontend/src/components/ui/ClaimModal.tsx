import React, { useState } from 'react';
import { claimsApi } from '../../api/client';
import { Button } from './Button';
import { X, Lock, ShieldCheck, AlertCircle } from 'lucide-react';

interface ClaimModalProps {
  reportId: string;
  reportTitle: string;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const ClaimModal: React.FC<ClaimModalProps> = ({
  reportId,
  reportTitle,
  isOpen,
  onClose,
  onSuccess,
}) => {
  const [description, setDescription] = useState('');
  const [serialNumber, setSerialNumber] = useState('');
  const [identifyingMarks, setIdentifyingMarks] = useState('');
  const [additionalDetails, setAdditionalDetails] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!description.trim()) {
      setError('Please provide a claim description.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);
      await claimsApi.submit({
        reportId,
        claimDescription: description.trim(),
        privateEvidence: {
          serialNumber: serialNumber.trim() || undefined,
          identifyingMarks: identifyingMarks.trim() || undefined,
          additionalSecretDetails: additionalDetails.trim() || undefined,
        },
      });
      onSuccess();
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit ownership claim. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="relative bg-white rounded-xl shadow-xl max-w-lg w-full p-6 text-left border border-slate-200">
        {/* Header */}
        <div className="flex justify-between items-start pb-4 border-b border-slate-100">
          <div>
            <h3 className="text-base font-semibold text-slate-900">
              Submit Recovery Claim
            </h3>
            <p className="text-xs text-slate-500 mt-0.5 line-clamp-1">
              For item: <span className="font-medium text-slate-700">{reportTitle}</span>
            </p>
          </div>
          <button
            onClick={onClose}
            className="p-1 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-100"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {error && (
          <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-md flex items-center gap-2 text-xs text-red-700">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="mt-4 space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Why is this your item? *
            </label>
            <textarea
              required
              rows={3}
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Explain how and where you lost it, general contents or appearance..."
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border"
            />
          </div>

          {/* Privacy Protected Evidence Box */}
          <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg space-y-3">
            <div className="flex items-center gap-2 text-xs font-semibold text-slate-800">
              <Lock className="w-4 h-4 text-primary-600" />
              <span>Private Verification Evidence</span>
              <span className="text-[10px] text-slate-400 font-normal ml-auto">(Confidential)</span>
            </div>
            <p className="text-[11px] text-slate-500">
              These details will NEVER be shown publicly. Only the verified reporter or moderator will review this proof.
            </p>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Serial Number, IMEI, or Identifier (if applicable)
              </label>
              <input
                type="text"
                value={serialNumber}
                onChange={(e) => setSerialNumber(e.target.value)}
                placeholder="e.g. SN-987654321, Student ID number"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2 border bg-white"
              />
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Unique Marks or Stickers
              </label>
              <input
                type="text"
                value={identifyingMarks}
                onChange={(e) => setIdentifyingMarks(e.target.value)}
                placeholder="e.g. Astronaut sticker on top right, red keychain with initials"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2 border bg-white"
              />
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Hidden Contents or Secret Passcode Hint
              </label>
              <input
                type="text"
                value={additionalDetails}
                onChange={(e) => setAdditionalDetails(e.target.value)}
                placeholder="e.g. Wallpaper is photo of my dog, receipt in inner zipper"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2 border bg-white"
              />
            </div>
          </div>

          <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
            <Button type="button" variant="outline" size="sm" onClick={onClose} disabled={isSubmitting}>
              Cancel
            </Button>
            <Button type="submit" size="sm" isLoading={isSubmitting}>
              <ShieldCheck className="w-4 h-4" />
              Submit Ownership Claim
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
