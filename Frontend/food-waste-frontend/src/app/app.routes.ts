import { Routes } from '@angular/router';

// Guards
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { donorGuard } from './guards/donor-guard';
import { ngoGuard } from './guards/ngo-guard';
import { volunteerGuard } from './guards/volunteer-guard';

// Public Components
import { HomeComponent } from './components/home/home';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password';
import { AboutComponent } from './components/about/about';
import { ContactComponent } from './components/contact/contact';
import { NotFoundComponent } from './components/not-found/not-found';

// Common/Auth Component
import { NotificationsComponent } from './components/notifications/notifications';

// Donor Components
import { DonorDashboardComponent } from './components/donor/donor-dashboard/donor-dashboard';
import { DonorProfileComponent } from './components/donor/donor-profile/donor-profile';
import { FoodDonationComponent } from './components/donor/food-donation/food-donation';
import { DonationHistoryComponent } from './components/donor/donation-history/donation-history';

// NGO Components
import { NgoDashboardComponent } from './components/ngo/ngo-dashboard/ngo-dashboard';
import { FoodRequestComponent } from './components/ngo/food-request/food-request';
import { AvailableFoodComponent } from './components/ngo/available-food/available-food';

// Volunteer Components
import { VolunteerDashboardComponent } from './components/volunteer/volunteer-dashboard/volunteer-dashboard';
import { PickupRequestsComponent } from './components/volunteer/pickup-requests/pickup-requests';
import { PickupDetailsComponent } from './components/volunteer/pickup-details/pickup-details';

// Admin Components
import { AdminDashboardComponent } from './components/admin/admin-dashboard/admin-dashboard';
import { UserManagementComponent } from './components/admin/user-management/user-management';
import { DonationManagementComponent } from './components/admin/donation-management/donation-management';
import { NgoManagementComponent } from './components/admin/ngo-management/ngo-management';
import { VolunteerManagementComponent } from './components/admin/volunteer-management/volunteer-management';
import { ReportsComponent } from './components/admin/reports/reports';

export const routes: Routes = [

  // =========================
  // PUBLIC ROUTES
  // =========================

  {
    path: '',
    component: HomeComponent
  },

  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'register',
    component: RegisterComponent
  },

  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },

  {
    path: 'about',
    component: AboutComponent
  },

  {
    path: 'contact',
    component: ContactComponent
  },


  // =========================
  // AUTHENTICATED ROUTES
  // =========================

  {
    path: 'notifications',
    component: NotificationsComponent,
    canActivate: [authGuard]
  },


  // =========================
  // DONOR ROUTES
  // =========================

  {
    path: 'donor/dashboard',
    component: DonorDashboardComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/profile',
    component: DonorProfileComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/donate',
    component: FoodDonationComponent,
    canActivate: [authGuard, donorGuard]
  },

  {
    path: 'donor/history',
    component: DonationHistoryComponent,
    canActivate: [authGuard, donorGuard]
  },


  // =========================
  // NGO ROUTES
  // =========================

  {
    path: 'ngo/dashboard',
    component: NgoDashboardComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/requests',
    component: FoodRequestComponent,
    canActivate: [authGuard, ngoGuard]
  },

  {
    path: 'ngo/available-food',
    component: AvailableFoodComponent,
    canActivate: [authGuard, ngoGuard]
  },


  // =========================
  // VOLUNTEER ROUTES
  // =========================

  {
    path: 'volunteer/dashboard',
    component: VolunteerDashboardComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
    path: 'volunteer/pickups',
    component: PickupRequestsComponent,
    canActivate: [authGuard, volunteerGuard]
  },

  {
    path: 'volunteer/pickups/:id',
    component: PickupDetailsComponent,
    canActivate: [authGuard, volunteerGuard]
  },


  // =========================
  // ADMIN ROUTES
  // =========================

  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/users',
    component: UserManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/donations',
    component: DonationManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/ngos',
    component: NgoManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/volunteers',
    component: VolunteerManagementComponent,
    canActivate: [authGuard, adminGuard]
  },

  {
    path: 'admin/reports',
    component: ReportsComponent,
    canActivate: [authGuard, adminGuard]
  },


  // =========================
  // 404 - MUST BE LAST
  // =========================

  {
    path: '**',
    component: NotFoundComponent
  }

];