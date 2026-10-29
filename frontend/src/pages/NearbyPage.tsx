import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { reportsApi } from '../api/client';
import { ItemReport, ReportType } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { MapPin, Navigation, Search, ArrowRight } from 'lucide-react';

export const NearbyPage: React.FC = () => {
  const [longitude, setLongitude] = useState<number>(77.5946);
  const [latitude, setLatitude] = useState<number>(12.9716);
  const [radiusMeters, setRadiusMeters] = useState<number>(5000);
  const [reportType, setReportType] = useState<ReportType | ''>('');
  const [items, setItems] = useState<ItemReport[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchNearby = async () => {
    setIsLoading(true);
    try {
      const data = await reportsApi.getNearby({
        longitude,
        latitude,
        radiusMeters,
        reportType: reportType || undefined,
      });
      setItems(data);
    } catch (err) {
      console.error('Failed to fetch nearby items', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchNearby();
  }, []);

  const handleDetectLocation = () => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setLatitude(Math.round(pos.coords.latitude * 10000) / 10000);
          setLongitude(Math.round(pos.coords.longitude * 10000) / 10000);
        },
        () => alert('Could not retrieve browser geolocation.')
      );
    } else {
      alert('Geolocation is not supported by your browser.');
    }
  };

  return (
    <div className="space-y-6 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
          <MapPin className="w-6 h-6 text-primary-600" />
          Location-Aware Proximity Explorer
        </h1>
        <p className="text-xs text-slate-500 mt-1">
          Powered by MongoDB 2dsphere spherical geospatial indexing. Finds items reported within walking or driving distance.
        </p>
      </div>

      {/* Geospatial Query Controller */}
      <Card className="p-4 sm:p-5">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            fetchNearby();
          }}
          className="space-y-4"
        >
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 text-xs">
            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Longitude
              </label>
              <input
                type="number"
                step="0.0001"
                required
                value={longitude}
                onChange={(e) => setLongitude(parseFloat(e.target.value))}
                className="w-full text-xs rounded-md border-slate-300 p-2 border bg-white"
              />
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Latitude
              </label>
              <input
                type="number"
                step="0.0001"
                required
                value={latitude}
                onChange={(e) => setLatitude(parseFloat(e.target.value))}
                className="w-full text-xs rounded-md border-slate-300 p-2 border bg-white"
              />
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Search Radius
              </label>
              <select
                value={radiusMeters}
                onChange={(e) => setRadiusMeters(parseInt(e.target.value))}
                className="w-full text-xs rounded-md border-slate-300 p-2 border bg-white"
              >
                <option value="500">500 meters (Walking)</option>
                <option value="1000">1 km (Campus vicinity)</option>
                <option value="3000">3 km (Neighborhood)</option>
                <option value="5000">5 km (Town / Metro area)</option>
                <option value="10000">10 km (Wider region)</option>
              </select>
            </div>

            <div>
              <label className="block text-[11px] font-medium text-slate-700 mb-1">
                Type
              </label>
              <select
                value={reportType}
                onChange={(e) => setReportType(e.target.value as ReportType | '')}
                className="w-full text-xs rounded-md border-slate-300 p-2 border bg-white"
              >
                <option value="">Lost & Found</option>
                <option value="LOST">Lost Items Only</option>
                <option value="FOUND">Found Items Only</option>
              </select>
            </div>
          </div>

          <div className="flex items-center justify-between pt-2 border-t border-slate-100">
            <button
              type="button"
              onClick={handleDetectLocation}
              className="text-xs text-primary-600 hover:text-primary-800 flex items-center gap-1 font-medium"
            >
              <Navigation className="w-3.5 h-3.5" /> Detect My GPS Location
            </button>

            <Button type="submit" size="sm" isLoading={isLoading}>
              <Search className="w-3.5 h-3.5 mr-1" /> Run Geospatial Query
            </Button>
          </div>
        </form>
      </Card>

      {/* Results */}
      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Querying 2dsphere index...</p>
        </div>
      ) : items.length === 0 ? (
        <EmptyState
          icon={MapPin}
          title="No Items Found Nearby"
          description={`No active lost or found reports were found within ${radiusMeters >= 1000 ? `${radiusMeters / 1000} km` : `${radiusMeters} m`} of coordinates [${longitude}, ${latitude}]. Try expanding the radius.`}
        />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {items.map((item) => (
            <Link key={item.id} to={`/reports/${item.id}`}>
              <Card className="hover:shadow-md transition-shadow h-full flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <Badge type={item.reportType} />
                    <Badge status={item.status} />
                  </div>
                  <h3 className="font-semibold text-slate-900 text-sm line-clamp-1">
                    {item.title}
                  </h3>
                  <div className="text-xs text-slate-500 mt-1">
                    <span>{item.categoryName}</span>
                    {item.brand && <span> • {item.brand}</span>}
                  </div>
                  <p className="mt-2 text-xs text-slate-600 line-clamp-2">
                    {item.description}
                  </p>
                </div>

                <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
                  <span className="flex items-center gap-1 truncate">
                    <MapPin className="w-3.5 h-3.5 text-primary-600 flex-shrink-0" />
                    {item.publicLocationLabel}
                  </span>
                  <span className="text-primary-600 font-semibold flex items-center gap-0.5 flex-shrink-0">
                    View <ArrowRight className="w-3 h-3" />
                  </span>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};
