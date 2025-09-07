import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = 'http://localhost:8082/api/v1/company';
  private tokenKey = 'jwt_token';

  constructor(private http: HttpClient) {}

  signup(data: { name: string; email: string; password: string }) {
    return this.http.post(`${this.apiUrl}/signup`, data)
      .pipe(
        tap((id: any) => {
          // Persist company identity for subsequent requests
          localStorage.setItem('company_id', id);
          localStorage.setItem('company_name', data.name);
        })
      );
  }

  signin(data: { name: string; password: string }) {
    return this.http.post<{ token: string }>(`${this.apiUrl}/signin`, { name: data.name, password: data.password })
      .pipe(
        tap(res => {
          localStorage.setItem(this.tokenKey, res.token);
          localStorage.setItem('company_name', data.name);
          try {
            const payload = JSON.parse(atob(res.token.split('.')[1]));
            const companyId = payload.sub;
            if (companyId) {
              localStorage.setItem('company_id', companyId);
            }
          } catch (e) {
            // ignore decode errors
          }
        })
      );
  }

  getToken() {
    return localStorage.getItem(this.tokenKey);
  }

  logout() {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('company_id');
    localStorage.removeItem('company_name');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}