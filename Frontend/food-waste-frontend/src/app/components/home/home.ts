import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Auth } from '../../services/auth';
import { Report } from '../../services/report';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    RouterLink,
    CommonModule
  ],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {

  stats: any = {
    totalDonations: 0,
    totalPickups: 0,
    activeNgos: 0,
    activeVolunteers: 0
  };

  constructor(
    private authService: Auth,
    private reportService: Report,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      const role = this.authService.getUserRole();
      if (role === 'DONOR') {
        this.router.navigate(['/donor/dashboard']);
      } else if (role === 'NGO') {
        this.router.navigate(['/ngo/dashboard']);
      } else if (role === 'VOLUNTEER') {
        this.router.navigate(['/volunteer/dashboard']);
      } else if (role === 'ADMIN') {
        this.router.navigate(['/admin/dashboard']);
      }
    }

    this.loadStats();
  }

  loadStats(): void {
    this.reportService.getSystemStats().subscribe({
      next: (data) => {
        if (data) {
          this.stats = data;
        }
      },
      error: (err) => {
        console.error('Error loading home impact metrics:', err);
      }
    });
  }
}
