import React, { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { reportsApi, categoriesApi } from '../api/client';
import { ItemReport, Category, ReportType, ReportStatus } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { Search, MapPin, Filter, ChevronLeft, ChevronRight, RefreshCw } from 'lucide-react';

export const BrowsePage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [reports, setReports] = useState<ItemReport[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLoading, setIsLoading] = useState(true);

  // Filters state
  const [keyword, setKeyword] = useState(searchParams.get('q') || '');
  const [reportType, setReportType] = useState<ReportType | ''>(
    (searchParams.get('type') as ReportType) || ''
  );
  const [categoryId, setCategoryId] = useState(searchParams.get('category') || '');
  const [status, setStatus] = useState<ReportStatus | ''>('');

  useEffect(() => {
    categoriesApi.getAll()
      .then(setCategories)
      .catch((err) => console.error('Error fetching categories', err));
  }, []);

  const loadReports = async (page = 0) => {
    setIsLoading(true);
    try {
      const data = await reportsApi.search({
        keyword: keyword.trim() || undefined,
        reportType: reportType || undefined,
        categoryId: categoryId || undefined,
        status: status || undefined,
        page,
        size: 9,
      });
      setReports(data.content);
      setTotalElements(data.totalElements);
      setTotalPages(data.totalPages);
      setCurrentPage(data.page);
    } catch (err) {
      console.error('Failed to load reports', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadReports(0);
  }, [reportType, categoryId, status]);

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    loadReports(0);
  };

  const handleReset = () => {
    setKeyword('');
    setReportType('');
    setCategoryId('');
    setStatus('');
    setSearchParams({});
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-navy-900">Browse Community Reports</h1>
          <p className="text-xs text-slate-500 mt-1">
            Discover lost belongings and found items across the platform ({totalElements} items listed)
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Link to="/reports/new-lost">
            <Button size="sm" variant="outline">Report Lost</Button>
          </Link>
          <Link to="/reports/new-found">
            <Button size="sm">Report Found</Button>
          </Link>
        </div>
      </div>

      {/* Filter Bar */}
      <Card className="p-4">
        <form onSubmit={handleSearchSubmit} className="space-y-3">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="Search item title, brand, color, or description..."
                className="w-full pl-9 pr-3 py-2 text-xs rounded-md border border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 bg-white"
              />
            </div>
            <Button type="submit" size="sm">
              Search
            </Button>
            <Button type="button" variant="outline" size="sm" onClick={handleReset} title="Reset Filters">
              <RefreshCw className="w-3.5 h-3.5" />
            </Button>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 pt-2 border-t border-slate-100 text-xs">
            {/* Type selector */}
            <div>
              <label className="block text-[11px] font-medium text-slate-600 mb-1">Report Type</label>
              <select
                value={reportType}
                onChange={(e) => setReportType(e.target.value as ReportType | '')}
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-1.5 border bg-white"
              >
                <option value="">All Types (Lost & Found)</option>
                <option value="LOST">Lost Items Only</option>
                <option value="FOUND">Found Items Only</option>
              </select>
            </div>

            {/* Category selector */}
            <div>
              <label className="block text-[11px] font-medium text-slate-600 mb-1">Category</label>
              <select
                value={categoryId}
                onChange={(e) => setCategoryId(e.target.value)}
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-1.5 border bg-white"
              >
                <option value="">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>

            {/* Status selector */}
            <div className="col-span-2 sm:col-span-1">
              <label className="block text-[11px] font-medium text-slate-600 mb-1">Status</label>
              <select
                value={status}
                onChange={(e) => setStatus(e.target.value as ReportStatus | '')}
                className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-1.5 border bg-white"
              >
                <option value="">Active & Matched</option>
                <option value="ACTIVE">Active</option>
                <option value="MATCHED">Matched</option>
                <option value="CLAIM_PENDING">Claim Pending</option>
                <option value="RETURNED">Returned / Recovered</option>
              </select>
            </div>
          </div>
        </form>
      </Card>

      {/* Item List / Grid */}
      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Searching MongoDB reports...</p>
        </div>
      ) : reports.length === 0 ? (
        <EmptyState
          title="No Matching Reports Found"
          description="Try broadening your search keywords or clearing active category filters."
          actionText="Reset Filters"
          onAction={handleReset}
        />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
          {reports.map((report) => (
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
                  <div className="flex items-center gap-2 text-[11px] text-slate-500 mt-1">
                    <span className="font-medium text-slate-700">{report.categoryName}</span>
                    {report.brand && <span>• {report.brand}</span>}
                    {report.color && <span>• {report.color}</span>}
                  </div>
                  <p className="mt-2 text-xs text-slate-600 line-clamp-2">
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

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between pt-4 border-t border-slate-200">
          <p className="text-xs text-slate-500">
            Page <span className="font-medium">{currentPage + 1}</span> of <span className="font-medium">{totalPages}</span>
          </p>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage === 0}
              onClick={() => loadReports(currentPage - 1)}
            >
              <ChevronLeft className="w-4 h-4" /> Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              disabled={currentPage >= totalPages - 1}
              onClick={() => loadReports(currentPage + 1)}
            >
              Next <ChevronRight className="w-4 h-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
};
