import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const ANALYTICS_API_BASE = 'http://localhost:8084/api/analytics';

// Interfaces for API responses
export interface AdminDashboardData {
  totalUsers: number;
  candidatesCount: number;
  recruitersCount: number;
  userRoleChart: {
    labels: string[];
    data: number[];
  };
}

export interface RecruiterDashboardData {
  jobOffers: {
    id: number;
    title: string;
    applicationsCount: number;
  }[];
  totalApplications: number;
  scoreDistribution: {
    labels: string[];
    data: number[];
  };
}

export interface CandidateDashboardData {
  trendingJobTitles: {
    labels: string[];
    data: number[];
  };
  trendingTechnologies: {
    labels: string[];
    data: number[];
  };
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  constructor(private http: HttpClient) { }

  /**
   * Get admin dashboard data
   */
  getAdminDashboard(): Observable<AdminDashboardData> {
    return this.http.get<AdminDashboardData>(`${ANALYTICS_API_BASE}/admin/dashboard`);
  }

  /**
   * Get recruiter dashboard data
   */
  getRecruiterDashboard(recruiterId: number): Observable<RecruiterDashboardData> {
    return this.http.get<RecruiterDashboardData>(`${ANALYTICS_API_BASE}/recruiter/${recruiterId}/dashboard`);
  }

  /**
   * Get candidate dashboard data
   */
  getCandidateDashboard(): Observable<CandidateDashboardData> {
    return this.http.get<CandidateDashboardData>(`${ANALYTICS_API_BASE}/candidate/dashboard`);
  }

  /**
   * Health check for analytics service
   */
  checkHealth(): Observable<any> {
    return this.http.get(`http://localhost:8084/health`);
  }
}
