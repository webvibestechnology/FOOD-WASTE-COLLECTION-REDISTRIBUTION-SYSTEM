import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';

import { Auth } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    RouterLink,
    NgIf
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent implements OnInit {

  isLoggedIn: boolean = false;
  userRole: string | null = null;
  userName: string = '';

  constructor(
    private authService: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isLoggedIn();
    this.userRole = this.authService.getUserRole();

    const user = this.authService.getCurrentUser();
    this.userName = user?.name || '';
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.userRole = null;
    this.userName = '';

    this.router.navigate(['/login']);
  }

  get isAdmin(): boolean {
    return this.userRole === 'ADMIN';
  }

  get isDonor(): boolean {
    return this.userRole === 'DONOR';
  }

  get isNgo(): boolean {
    return this.userRole === 'NGO';
  }

  get isVolunteer(): boolean {
    return this.userRole === 'VOLUNTEER';
  }
}