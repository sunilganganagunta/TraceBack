import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import {
  ApiResponse,
  AuthResponse,
  Category,
  Claim,
  CreateReportRequest,
  ItemReport,
  Match,
  Notification,
  PagedResponse,
  User,
  AnalyticsSummary,
  ClaimStatus,
  ReportStatus,
  ReportType,
  PrivateVerificationEvidence,
} from '../types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('traceback_token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor: Standard error handling
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<{ message?: string }>) => {
    if (error.response?.status === 401) {
      const path = window.location.pathname;
      if (path !== '/login' && path !== '/register' && path !== '/') {
        localStorage.removeItem('traceback_token');
        localStorage.removeItem('traceback_user');
      }
    }
    return Promise.reject(error);
  }
);

export default api;

/* --- API Methods --- */

// Authentication
export const authApi = {
  register: async (data: { name: string; email: string; password: string }) => {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/register', data);
    return res.data.data;
  },
  login: async (data: { email: string; password: string }) => {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', data);
    return res.data.data;
  },
  getMe: async () => {
    const res = await api.get<ApiResponse<User>>('/auth/me');
    return res.data.data;
  },
};

// Categories
export const categoriesApi = {
  getAll: async () => {
    const res = await api.get<ApiResponse<Category[]>>('/categories');
    return res.data.data;
  },
  getAllAdmin: async () => {
    const res = await api.get<ApiResponse<Category[]>>('/categories/admin/all');
    return res.data.data;
  },
  create: async (category: Partial<Category>) => {
    const res = await api.post<ApiResponse<Category>>('/categories', category);
    return res.data.data;
  },
  update: async (id: string, category: Partial<Category>) => {
    const res = await api.put<ApiResponse<Category>>(`/categories/${id}`, category);
    return res.data.data;
  },
  delete: async (id: string) => {
    const res = await api.delete<ApiResponse<void>>(`/categories/${id}`);
    return res.data;
  },
};

// Reports
export const reportsApi = {
  search: async (params: {
    keyword?: string;
    reportType?: ReportType;
    categoryId?: string;
    status?: ReportStatus;
    page?: number;
    size?: number;
  }) => {
    const res = await api.get<ApiResponse<PagedResponse<ItemReport>>>('/reports', { params });
    return res.data.data;
  },
  getNearby: async (params: {
    longitude: number;
    latitude: number;
    radiusMeters?: number;
    reportType?: ReportType;
  }) => {
    const res = await api.get<ApiResponse<ItemReport[]>>('/reports/nearby', { params });
    return res.data.data;
  },
  getMyReports: async (page = 0, size = 12) => {
    const res = await api.get<ApiResponse<PagedResponse<ItemReport>>>('/reports/my-reports', {
      params: { page, size },
    });
    return res.data.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<ItemReport>>(`/reports/${id}`);
    return res.data.data;
  },
  create: async (data: CreateReportRequest) => {
    const res = await api.post<ApiResponse<ItemReport>>('/reports', data);
    return res.data.data;
  },
  update: async (id: string, data: Partial<CreateReportRequest>) => {
    const res = await api.put<ApiResponse<ItemReport>>(`/reports/${id}`, data);
    return res.data.data;
  },
  updateStatus: async (id: string, status: ReportStatus) => {
    const res = await api.patch<ApiResponse<ItemReport>>(`/reports/${id}/status`, null, {
      params: { status },
    });
    return res.data.data;
  },
  withdraw: async (id: string) => {
    const res = await api.delete<ApiResponse<void>>(`/reports/${id}`);
    return res.data;
  },
};

// Matches
export const matchesApi = {
  getForReport: async (reportId: string) => {
    const res = await api.get<ApiResponse<Match[]>>(`/matches/report/${reportId}`);
    return res.data.data;
  },
  getMyMatches: async () => {
    const res = await api.get<ApiResponse<Match[]>>('/matches/my-matches');
    return res.data.data;
  },
  recalculate: async (reportId: string) => {
    const res = await api.post<ApiResponse<Match[]>>(`/matches/recalculate/${reportId}`);
    return res.data.data;
  },
};

// Claims
export const claimsApi = {
  submit: async (data: {
    reportId: string;
    claimDescription: string;
    privateEvidence?: PrivateVerificationEvidence;
  }) => {
    const res = await api.post<ApiResponse<Claim>>('/claims', data);
    return res.data.data;
  },
  getMyClaims: async (page = 0, size = 12) => {
    const res = await api.get<ApiResponse<PagedResponse<Claim>>>('/claims/my-claims', {
      params: { page, size },
    });
    return res.data.data;
  },
  getForReport: async (reportId: string) => {
    const res = await api.get<ApiResponse<Claim[]>>(`/claims/report/${reportId}`);
    return res.data.data;
  },
  getById: async (id: string) => {
    const res = await api.get<ApiResponse<Claim>>(`/claims/${id}`);
    return res.data.data;
  },
  review: async (id: string, data: { status: ClaimStatus; reviewNotes?: string }) => {
    const res = await api.patch<ApiResponse<Claim>>(`/claims/${id}/status`, data);
    return res.data.data;
  },
  getModerationClaims: async (status?: ClaimStatus, page = 0, size = 20) => {
    const res = await api.get<ApiResponse<PagedResponse<Claim>>>('/claims/moderation/all', {
      params: { status, page, size },
    });
    return res.data.data;
  },
};

// Notifications
export const notificationsApi = {
  getAll: async (page = 0, size = 20) => {
    const res = await api.get<ApiResponse<PagedResponse<Notification>>>('/notifications', {
      params: { page, size },
    });
    return res.data.data;
  },
  getUnreadCount: async () => {
    const res = await api.get<ApiResponse<{ unreadCount: number }>>('/notifications/unread-count');
    return res.data.data.unreadCount;
  },
  markAsRead: async (id: string) => {
    const res = await api.patch<ApiResponse<Notification>>(`/notifications/${id}/read`);
    return res.data.data;
  },
  markAllAsRead: async () => {
    const res = await api.patch<ApiResponse<void>>('/notifications/mark-all-read');
    return res.data;
  },
};

// Analytics
export const analyticsApi = {
  getSummary: async () => {
    const res = await api.get<ApiResponse<AnalyticsSummary>>('/analytics/summary');
    return res.data.data;
  },
};

// Users
export const usersApi = {
  getMe: async () => {
    const res = await api.get<ApiResponse<User>>('/users/me');
    return res.data.data;
  },
  updateProfile: async (data: { name?: string; phone?: string; profileImage?: string }) => {
    const res = await api.patch<ApiResponse<User>>('/users/me', data);
    return res.data.data;
  },
  adminGetAll: async (page = 0, size = 20) => {
    const res = await api.get<ApiResponse<PagedResponse<User>>>('/users/admin/all', {
      params: { page, size },
    });
    return res.data.data;
  },
  adminUpdateStatus: async (id: string, status: 'ACTIVE' | 'SUSPENDED') => {
    const res = await api.patch<ApiResponse<User>>(`/users/admin/${id}/status`, null, {
      params: { status },
    });
    return res.data.data;
  },
};
