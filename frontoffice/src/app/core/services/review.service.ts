import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private publicApi = 'http://localhost:8081/api/v1/reviews';
  private companyApi = 'http://localhost:8082/api/v1/reviews';

  constructor(private http: HttpClient) {}

  // Build query params dynamically
  private buildParams(page: number, size: number, filters: any): HttpParams {
    let params = new HttpParams().set('page', page).set('size', size);
    Object.keys(filters).forEach(key => {
      if (filters[key]) {
        params = params.set(key, filters[key]);
      }
    });
    return params;
  }

  getPublicReviews(page: number, size: number, filters: any): Observable<any> {
    const params = this.buildParams(page, size, filters);
    return this.http.get(this.publicApi, { params });
  }

  getCompanyReviews(page: number, size: number, filters: any): Observable<any> {
    const params = this.buildParams(page, size, filters);
    const companyName = localStorage.getItem('company_name');
    const headers = companyName ? new HttpHeaders({ 'Company-Name': companyName }) : undefined;
    return this.http.get(this.companyApi, { params, headers });
  }

  getReview(id: string): Observable<any> {
    return this.http.get(`${this.companyApi}/${id}`);
  }

  respondToReview(id: string, response: string, newState: string): Observable<any> {
    return this.http.put(`${this.companyApi}/${id}/response`, { 
      responseText: response, 
      newState: newState 
    });
  }

  submitReview(review: any): Observable<any> {
    return this.http.post(this.publicApi, review);
  }

  getFlights(): Observable<any> {
    return this.http.get(`${this.publicApi}/flights`);
  }
}
