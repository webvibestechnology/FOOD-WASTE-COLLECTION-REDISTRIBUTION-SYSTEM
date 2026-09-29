import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/login`,
      {
        email: email,
        password: password
      }
    );
  }

  register(
    name: string,
    email: string,
    password: string,
    phone: string,
    role: string
  ): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/register`,
      {
        name: name,
        email: email,
        password: password,
        phone: phone,
        role: role
      }
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getCurrentUser(): any {
    const user = localStorage.getItem('user');

    if (user) {
      return JSON.parse(user);
    }

    return null;
  }

  getUserRole(): string | null {
    const user = this.getCurrentUser();

    return user?.role || null;
  }

  isAdmin(): boolean {
    return this.getUserRole() === 'ADMIN';
  }

  isDonor(): boolean {
    return this.getUserRole() === 'DONOR';
  }

  isNgo(): boolean {
    return this.getUserRole() === 'NGO';
  }

  isVolunteer(): boolean {
    return this.getUserRole() === 'VOLUNTEER';
  }

  saveSession(token: string, user: any): void {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
  }
}