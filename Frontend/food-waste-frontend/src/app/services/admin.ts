import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Admin {

  private baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/users`
    );
  }

  toggleUserStatus(id: number): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/users/${id}/toggle`,
      {}
    );
  }

  verifyNgo(ngoId: number): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/ngos/${ngoId}/verify`,
      {}
    );
  }

  getDashboardStats(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/dashboard`
    );
  }

  getAllDonations(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/donations`
    );
  }

  getAllPickups(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/pickups`
    );
  }
}