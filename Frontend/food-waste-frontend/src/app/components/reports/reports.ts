import { Component, OnInit } from '@angular/core';
import { Report } from '../../services/report';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [],
  templateUrl: './reports.html',
  styleUrl: './reports.css'
})
export class ReportsComponent implements OnInit {

  stats: any = null;
  isLoading = false;

  constructor(
    private reportService: Report
  ) {}

  ngOnInit(): void {
    this.isLoading = true;

    this.reportService.getSystemStats().subscribe({
      next: (data: any) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading reports:', error);
        this.isLoading = false;
      }
    });
  }
}