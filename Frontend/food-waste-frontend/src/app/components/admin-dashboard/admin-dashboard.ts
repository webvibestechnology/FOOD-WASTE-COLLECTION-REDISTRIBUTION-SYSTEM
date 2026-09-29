import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Admin } from '../../services/admin';
import { Report } from '../../services/report';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css'
})
export class AdminDashboard implements OnInit {

  stats: any = {};
  recentDonations: any[] = [];
  recentPickups: any[] = [];

  constructor(
    private adminService: Admin,
    private reportService: Report
  ) {}

  ngOnInit(): void {
    this.loadStats();
    this.loadRecentDonations();
    this.loadRecentPickups();
  }

  loadStats(): void {
    this.reportService.getSystemStats().subscribe({
      next: (data: any) => {
        this.stats = data;
      },
      error: (error: any) => {
        console.error('Error loading statistics:', error);
      }
    });
  }

  loadRecentDonations(): void {
    this.adminService.getAllDonations().subscribe({
      next: (data: any) => {
        this.recentDonations = data.slice(0, 5);
      },
      error: (error: any) => {
        console.error('Error loading donations:', error);
      }
    });
  }

  loadRecentPickups(): void {
    this.adminService.getAllPickups().subscribe({
      next: (data: any) => {
        this.recentPickups = data.slice(0, 5);
      },
      error: (error: any) => {
        console.error('Error loading pickups:', error);
      }
    });
  }
}