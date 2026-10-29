import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { reportsApi, categoriesApi } from '../api/client';
import { Category, CreateReportRequest } from '../types';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { MapPin, CheckCircle2, AlertCircle } from 'lucide-react';

export const ReportFoundPage: React.FC = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<Category[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<CreateReportRequest>({
    reportType: 'FOUND',
    title: '',
    categoryId: '',
    description: '',
    color: '',
    brand: '',
    distinguishingFeatures: '',
    dateLostOrFound: new Date().toISOString().split('T')[0],
    longitude: 77.5946,
    latitude: 12.9716,
    publicLocationLabel: '',
    images: [],
  });

  const [imageUrl, setImageUrl] = useState('');

  useEffect(() => {
    categoriesApi.getAll()
      .then((data) => {
        setCategories(data);
        if (data.length > 0) {
          setFormData((prev) => ({ ...prev, categoryId: data[0].id }));
        }
      })
      .catch((err) => console.error(err));
  }, []);

  const handleGetLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setFormData((prev) => ({
            ...prev,
            latitude: Math.round(pos.coords.latitude * 10000) / 10000,
            longitude: Math.round(pos.coords.longitude * 10000) / 10000,
          }));
        },
        () => alert('Could not retrieve current location. Using default coordinates.')
      );
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.title.trim() || !formData.categoryId || !formData.description.trim() || !formData.color.trim()) {
      setError('Please fill in all required fields.');
      return;
    }

    try {
      setIsSubmitting(true);
      setError(null);

      const payload = {
        ...formData,
        images: imageUrl.trim() ? [imageUrl.trim()] : [],
      };

      const created = await reportsApi.create(payload);
      navigate(`/reports/${created.id}`);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit found report');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
          <CheckCircle2 className="w-6 h-6 text-emerald-600" />
          Report a Found Item
        </h1>
        <p className="text-xs text-slate-500 mt-1">
          Thank you for helping someone recover their belongings. List the found item below so owners can verify and claim it.
        </p>
      </div>

      {error && (
        <div className="p-3 bg-red-50 border border-red-200 text-red-700 rounded-lg text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 flex-shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <Card className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Item Title *
            </label>
            <input
              type="text"
              required
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              placeholder="e.g. Found Apple Laptop in Central Library"
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Category *
              </label>
              <select
                required
                value={formData.categoryId}
                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
              >
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Date Found *
              </label>
              <input
                type="date"
                required
                max={new Date().toISOString().split('T')[0]}
                value={formData.dateLostOrFound}
                onChange={(e) => setFormData({ ...formData, dateLostOrFound: e.target.value })}
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
              />
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Color *
              </label>
              <input
                type="text"
                required
                value={formData.color}
                onChange={(e) => setFormData({ ...formData, color: e.target.value })}
                placeholder="e.g. Silver, Black, Red"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 mb-1">
                Brand / Manufacturer (if visible)
              </label>
              <input
                type="text"
                value={formData.brand}
                onChange={(e) => setFormData({ ...formData, brand: e.target.value })}
                placeholder="e.g. Apple, Dell, Titan"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              General Appearance / Features
            </label>
            <input
              type="text"
              value={formData.distinguishingFeatures}
              onChange={(e) => setFormData({ ...formData, distinguishingFeatures: e.target.value })}
              placeholder="e.g. Has stickers, placed in black protective sleeve"
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          {/* Location Area */}
          <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-lg space-y-3">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-800 flex items-center gap-1.5">
                <MapPin className="w-4 h-4 text-primary-600" />
                Found Location & Safe Custody
              </span>
              <button
                type="button"
                onClick={handleGetLocation}
                className="text-[11px] font-medium text-primary-600 hover:text-primary-800 underline"
              >
                Use My GPS Location
              </button>
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Public Location & Custody Label *
              </label>
              <input
                type="text"
                required
                value={formData.publicLocationLabel}
                onChange={(e) => setFormData({ ...formData, publicLocationLabel: e.target.value })}
                placeholder="e.g. Central Library Reception Desk, Table 24"
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2 border bg-white"
              />
            </div>

            <div className="grid grid-cols-2 gap-3 text-xs">
              <div>
                <label className="block text-[10px] text-slate-500 mb-0.5">Longitude</label>
                <input
                  type="number"
                  step="0.0001"
                  required
                  value={formData.longitude}
                  onChange={(e) => setFormData({ ...formData, longitude: parseFloat(e.target.value) })}
                  className="w-full text-xs rounded border-slate-300 p-1.5 border bg-white"
                />
              </div>
              <div>
                <label className="block text-[10px] text-slate-500 mb-0.5">Latitude</label>
                <input
                  type="number"
                  step="0.0001"
                  required
                  value={formData.latitude}
                  onChange={(e) => setFormData({ ...formData, latitude: parseFloat(e.target.value) })}
                  className="w-full text-xs rounded border-slate-300 p-1.5 border bg-white"
                />
              </div>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Description & How to Claim *
            </label>
            <textarea
              required
              rows={4}
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Describe where you found the item, its current safe custody location, and general details..."
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Image URL (Optional)
            </label>
            <input
              type="url"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
              placeholder="https://images.unsplash.com/photo-..."
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          <div className="pt-4 border-t border-slate-100 flex items-center justify-end gap-3">
            <Button type="button" variant="outline" size="sm" onClick={() => navigate(-1)}>
              Cancel
            </Button>
            <Button type="submit" size="sm" isLoading={isSubmitting} className="bg-emerald-600 hover:bg-emerald-700">
              Publish Found Report
            </Button>
          </div>
        </Card>
      </form>
    </div>
  );
};
