import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ReviewFormComponent } from './public/review-form/review-form.component';
import { ReviewListComponent } from './public/review-list/review-list.component';
import { SignupComponent } from './company/signup/signup.component';
import { SigninComponent } from './company/signin/signin.component';
import { DashboardComponent } from './company/dashboard/dashboard.component';
import { ReviewDetailComponent } from './company/review-detail/review-detail.component';
import { AuthGuard } from './core/guards/auth.guard';

const routes: Routes = [
  { path: '', component: ReviewListComponent },
  { path: 'submit', component: ReviewFormComponent },
  { path: 'company/signup', component: SignupComponent },
  { path: 'company/signin', component: SigninComponent },
  { path: 'company/dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'company/review/:id', component: ReviewDetailComponent, canActivate: [AuthGuard] }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
