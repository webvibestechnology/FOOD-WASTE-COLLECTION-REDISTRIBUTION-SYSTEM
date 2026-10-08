import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Notification {

  private baseUrl = 'http://localhost:8080/api/notifications';

  constructor(private http: HttpClient) {}

  getMyNotifications(): Observable<any> {
    return this.http.get<any>(
      this.baseUrl
    );
  }

  getUnreadNotifications(): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/unread`
    );
  }

  markAsRead(id: number): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/${id}/read`,
      {}
    );
  }

  markAllAsRead(): Observable<any> {
    return this.http.patch<any>(
      `${this.baseUrl}/read-all`,
      {}
    );
  }
}