import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FoodRequest {

  private baseUrl = 'http://localhost:8080/api/food-requests';

  constructor(private http: HttpClient) {}

  getMyRequests(): Observable<any> {
<<<<<<< HEAD
    return this.http.get<any>(
      `${this.baseUrl}/my`
    );
  }

  createRequest(
    donationId: number,
    message: string
  ): Observable<any> {
    return this.http.post<any>(
      this.baseUrl,
      {
        donationId: donationId,
        message: message
      }
    );
  }
}
=======
    return this.http.get<any>(`${this.baseUrl}/my`);
  }

  getIncomingRequests(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/incoming`);
  }

  createRequest(donationId: number, notes: string = '', requestedQuantity?: number, beneficiaryCount?: number, deliveryNotes?: string): Observable<any> {
    return this.http.post<any>(this.baseUrl, {
      donationId,
      notes,
      requestedQuantity,
      beneficiaryCount,
      deliveryNotes
    });
  }

  getAllRequests(): Observable<any> {
    return this.http.get<any>(this.baseUrl);
  }

  approveRequest(requestId: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${requestId}/approve`, {});
  }

  rejectRequest(requestId: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${requestId}/reject`, {});
  }

  cancelRequest(requestId: number): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${requestId}/cancel`, {});
  }
}
>>>>>>> 64475a0 (complete)
