import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class User {

  private baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUserById(id: number): Observable<any> {
    return this.http.get<any>(
      `${this.baseUrl}/${id}`
    );
  }

  updateUser(
    id: number,
    data: any
  ): Observable<any> {
    return this.http.put<any>(
      `${this.baseUrl}/${id}`,
      data
    );
  }
}