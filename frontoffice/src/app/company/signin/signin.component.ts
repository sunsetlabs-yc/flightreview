import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html'
})
export class SigninComponent {
  form = { name: '', password: '' };

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.signin(this.form).subscribe({
      next: () => {
        alert('Signin successful!');
        this.router.navigate(['/company/dashboard']);
      },
      error: err => console.error(err)
    });
  }
}
