import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMovement } from 'app/shared/model/movement.model';

@Component({
    selector: 'jhi-movement-detail',
    templateUrl: './movement-detail.component.html'
})
export class MovementDetailComponent implements OnInit {
    movement: IMovement;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ movement }) => {
            this.movement = movement;
        });
    }

    previousState() {
        window.history.back();
    }
}
