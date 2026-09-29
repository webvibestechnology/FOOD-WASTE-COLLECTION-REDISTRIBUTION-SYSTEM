import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Donation {

  private baseUrl = 'http://localhost:8080/api/donations';

  constructor(private http: HttpClient) {}

  createDonation(data: any): Observable<any> {
    return this.http.post<any>(
      this.baseUrl,
      data
    );
  }

  getAllDonations(): Observable<any> {
    return this.http.get<any>(
      this.baseUrl
    );
  }

  getMyDonations(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/my`
    );
  }

  getDonationById(id: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/${id}`
    );
  }

  updateDonationStatus(
    id: number,
    status: string
  ): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/${id}/status?status=${status}`,
      {}
    );
  }

  deleteDonation(id: number): Observable<any> {
    return this.http.delete<any>(
      `${this.baseUrl}/${id}`
    );
  }
}