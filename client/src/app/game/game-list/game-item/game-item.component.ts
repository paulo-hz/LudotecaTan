import { Component, input } from '@angular/core';
import { Game } from '../../model/game';
import {MatCardModule} from '@angular/material/card';

@Component({
    selector: 'app-game-item',
    standalone: true,
    imports: [MatCardModule],
    templateUrl: './game-item.component.html',
    styleUrl: './game-item.component.scss'
})
export class GameItemComponent {
    public readonly game = input.required<Game>();
}
