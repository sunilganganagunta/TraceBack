export type Role = 'ROLE_USER' | 'ROLE_MODERATOR' | 'ROLE_ADMIN';
export type AccountStatus = 'ACTIVE' | 'SUSPENDED';

export interface User {
  id: string;
  name: string;
  email: string;
  roles: Role[];
  accountStatus: AccountStatus;
  emailVerified: boolean;
  phone?: string;
  profileImage?: string;
  createdAt: string;
  lastLoginAt?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description: string;
  icon?: string;
  active: boolean;
}

export type ReportType = 'LOST' | 'FOUND';
export type ReportStatus = 'ACTIVE' | 'MATCHED' | 'CLAIM_PENDING' | 'RETURNED' | 'WITHDRAWN' | 'CLOSED';
export type Visibility = 'PUBLIC' | 'RESTRICTED';

export interface ItemReport {
  id: string;
  ownerId: string;
  ownerName: string;
  reportType: ReportType;
  title: string;
  categoryId: string;
  categoryName: string;
  description: string;
  color: string;
  brand?: string;
  distinguishingFeatures?: string;
  dateLostOrFound: string;
  publicLocationLabel: string;
  approximateLongitude?: number;
  approximateLatitude?: number;
  exactLongitude?: number;
  exactLatitude?: number;
  images: string[];
  status: ReportStatus;
  visibility: Visibility;
  createdAt: string;
  updatedAt: string;
  owner: boolean;
}

export interface CreateReportRequest {
  reportType: ReportType;
  title: string;
  categoryId: string;
  description: string;
  color: string;
  brand?: string;
  distinguishingFeatures?: string;
  dateLostOrFound: string;
  longitude: number;
  latitude: number;
  publicLocationLabel: string;
  images?: string[];
}

export interface ScoreBreakdown {
  categoryScore: number;
  titleScore: number;
  descriptionScore: number;
  attributeScore: number;
  dateScore: number;
  proximityScore: number;
  total: number;
}

export type MatchStatus = 'ACTIVE' | 'DISMISSED' | 'CONFIRMED';

export interface ItemSummary {
  id: string;
  title: string;
  categoryName: string;
  color: string;
  brand?: string;
  locationLabel: string;
  image?: string;
}

export interface Match {
  id: string;
  lostReportId: string;
  foundReportId: string;
  score: number;
  scoreBreakdown: ScoreBreakdown;
  reasons: string[];
  status: MatchStatus;
  createdAt: string;
  lostReport?: ItemSummary;
  foundReport?: ItemSummary;
}

export type ClaimStatus = 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' | 'WITHDRAWN' | 'COMPLETED';

export interface PrivateVerificationEvidence {
  identifyingMarks?: string;
  serialNumber?: string;
  receiptOrProofUrl?: string;
  additionalSecretDetails?: string;
}

export interface Claim {
  id: string;
  reportId: string;
  reportTitle: string;
  claimantId: string;
  claimantName: string;
  claimDescription: string;
  privateVerificationEvidence?: PrivateVerificationEvidence;
  status: ClaimStatus;
  reviewerId?: string;
  reviewNotes?: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  claimant: boolean;
}

export type NotificationType = 'MATCH_FOUND' | 'CLAIM_SUBMITTED' | 'CLAIM_STATUS_UPDATED' | 'SYSTEM_ALERT';

export interface Notification {
  id: string;
  recipientId: string;
  type: NotificationType;
  title: string;
  message: string;
  relatedResourceId?: string;
  relatedResourceType?: string;
  read: boolean;
  createdAt: string;
  readAt?: string;
}

export interface CategoryStat {
  categoryName: string;
  count: number;
}

export interface StatusStat {
  status: string;
  count: number;
}

export interface TimelineStat {
  month: string;
  reportsCount: number;
  recoveriesCount: number;
}

export interface AnalyticsSummary {
  totalUsers: number;
  totalReports: number;
  totalLostReports: number;
  totalFoundReports: number;
  activeReports: number;
  recoveredItems: number;
  recoveryRate: number;
  totalClaims: number;
  pendingClaims: number;
  approvedClaims: number;
  potentialMatches: number;
  categoryDistribution: CategoryStat[];
  reportStatusDistribution: StatusStat[];
  monthlyTrends: TimelineStat[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
