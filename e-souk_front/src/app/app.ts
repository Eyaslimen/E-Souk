import { Component, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { CommonModule } from '@angular/common';
import { Footer } from './layouts/footer/footer';
@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [RouterOutlet, NavbarComponent,CommonModule,Footer],
})
export class App {
  protected readonly title = signal('E-Souk');
  showNavbar = true;
  constructor(private router: Router) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.showNavbar = !['/login', '/register'].includes(event.url);
      }
    });
  }
}