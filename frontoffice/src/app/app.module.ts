import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { ReviewFormComponent } from './public/review-form/review-form.component';
import { ReviewListComponent } from './public/review-list/review-list.component';
import { SignupComponent } from './company/signup/signup.component';
import { SigninComponent } from './company/signin/signin.component';
import { DashboardComponent } from './company/dashboard/dashboard.component';
import { ReviewDetailComponent } from './company/review-detail/review-detail.component';
import { TokenInterceptor } from './core/guards/token.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    ReviewFormComponent,
    ReviewListComponent,
    SignupComponent,
    SigninComponent,
    DashboardComponent,
    ReviewDetailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
  { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true }
    ],

  bootstrap: [AppComponent]
})
export class AppModule {}
