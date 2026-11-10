    import { Component, signal } from '@angular/core';
    import { RouterOutlet } from '@angular/router';
    import { HeaderComponent } from './core/header/header.component';

    @Component({
        selector: 'app-root',
        standalone: true,
        imports: [RouterOutlet, HeaderComponent],
        templateUrl: './app.component.html',
        styleUrl: './app.css'
    })
    export class AppComponent {
        protected readonly title = signal('Tutorial de Angular');
    }
