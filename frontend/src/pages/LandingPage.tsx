import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { reportsApi } from '../api/client';
import { ItemReport } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Search, MapPin, Sparkles, Shield, ArrowRight, PlusCircle, CheckCircle2 } from 'lucide-react';

export const LandingPage: React.FC = () => {
  const navigate = useNavigate();
  const [recentReports, setRecentReports] = useState<ItemReport[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    reportsApi.search({ page: 0, size: 6 })
      .then((data) => setRecentReports(data.content))
      .catch((err) => console.error(err))
      .finally(() => setIsLoading(false));
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/browse?q=${encodeURIComponent(searchQuery.trim())}`);
    } else {
      navigate('/browse');
    }
  };

  return (
    <div className="space-y-12 pb-12">
      {/* Hero Section */}
      <section className="bg-gradient-to-b from-white to-slate-50 border border-slate-200 rounded-2xl p-6 sm:p-12 text-center shadow-sm">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-primary-50 text-primary-700 text-xs font-semibold mb-4 border border-primary-200">
          <Sparkles className="w-3.5 h-3.5" />
          Intelligent Matching & Recovery Engine
        </div>
        <h1 className="text-3xl sm:text-5xl font-extrabold text-navy-900 tracking-tight max-w-3xl mx-auto leading-tight">
          Recover What You Lost. Return What You Found.
        </h1>
        <p className="mt-4 text-base sm:text-lg text-slate-600 max-w-2xl mx-auto">
          TraceBack connects lost belongings with found items using explainable multi-signal algorithms, geospatial proximity search, and privacy-first ownership verification.
        </p>

        {/* Global Search Bar */}
        <form onSubmit={handleSearch} className="mt-8 max-w-xl mx-auto flex gap-2">
          <div className="relative flex-1">
            <Search className="w-5 h-5 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search by item name, brand, color (e.g. MacBook, Blue Wallet)..."
              className="w-full pl-10 pr-4 py-3 rounded-lg border border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 text-sm bg-white"
            />
          </div>
          <Button type="submit" size="lg">
            Search
          </Button>
        </form>

        {/* Action Buttons */}
        <div className="mt-8 flex flex-wrap items-center justify-center gap-4">
          <Link to="/reports/new-lost">
            <Button size="lg" className="bg-rose-600 hover:bg-rose-700 text-white">
              <PlusCircle className="w-5 h-5 mr-1" />
              Report Lost Item
            </Button>
          </Link>
          <Link to="/reports/new-found">
            <Button size="lg" variant="secondary">
              <CheckCircle2 className="w-5 h-5 mr-1 text-emerald-600" />
              Report Found Item
            </Button>
          </Link>
        </div>
      </section>

      {/* 3 Core Pillars */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Card className="text-left">
          <div className="w-10 h-10 rounded-lg bg-primary-100 text-primary-700 flex items-center justify-center mb-3">
            <Sparkles className="w-5 h-5" />
          </div>
          <h2 className="text-base font-semibold text-slate-900">Multi-Signal Matching</h2>
          <p className="mt-2 text-xs text-slate-600 leading-relaxed">
            Deterministic 0–100 scoring based on category taxonomy, title keyword similarity, brand/color attributes, and date proximity.
          </p>
        </Card>

        <Card className="text-left">
          <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center mb-3">
            <MapPin className="w-5 h-5" />
          </div>
          <h2 className="text-base font-semibold text-slate-900">Geospatial Proximity</h2>
          <p className="mt-2 text-xs text-slate-600 leading-relaxed">
            MongoDB 2dsphere spherical indexes calculate exact distances while preserving privacy through approximate public location labels.
          </p>
        </Card>

        <Card className="text-left">
          <div className="w-10 h-10 rounded-lg bg-purple-100 text-purple-700 flex items-center justify-center mb-3">
            <Shield className="w-5 h-5" />
          </div>
          <h2 className="text-base font-semibold text-slate-900">Confidential Claims</h2>
          <p className="mt-2 text-xs text-slate-600 leading-relaxed">
            Verify true ownership with hidden serial numbers and distinguishing proof visible only to the verified reporter.
          </p>
        </Card>
      </section>

      {/* Recent Public Reports */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl font-bold text-navy-900">Recent Community Reports</h2>
            <p className="text-xs text-slate-500">Live items reported across campuses, transit hubs, and public spaces</p>
          </div>
          <Link to="/browse" className="text-sm font-semibold text-primary-600 hover:text-primary-800 flex items-center gap-1">
            View All <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        {isLoading ? (
          <div className="text-center py-12">
            <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
            <p className="mt-2 text-xs text-slate-500">Loading recent reports...</p>
          </div>
        ) : recentReports.length === 0 ? (
          <div className="text-center py-8 text-sm text-slate-500">
            No reports currently listed. Be the first to report an item!
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {recentReports.map((report) => (
              <Link key={report.id} to={`/reports/${report.id}`}>
                <Card className="hover:shadow-md transition-shadow h-full flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between gap-2 mb-2">
                      <Badge type={report.reportType} />
                      <Badge status={report.status} />
                    </div>
                    <h3 className="font-semibold text-slate-900 text-sm line-clamp-1">
                      {report.title}
                    </h3>
                    <p className="mt-1 text-xs text-slate-500 line-clamp-2">
                      {report.description}
                    </p>
                  </div>

                  <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-500">
                    <span className="flex items-center gap-1 line-clamp-1">
                      <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                      {report.publicLocationLabel}
                    </span>
                    <span className="text-[11px] flex-shrink-0">
                      {new Date(report.dateLostOrFound).toLocaleDateString()}
                    </span>
                  </div>
                </Card>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};
