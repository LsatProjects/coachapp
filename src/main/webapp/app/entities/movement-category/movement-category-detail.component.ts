import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMovementCategory } from 'app/shared/model/movement-category.model';

@Component({
    selector: 'jhi-movement-category-detail',
    templateUrl: './movement-category-detail.component.html'
})
export class MovementCategoryDetailComponent implements OnInit {
    movementCategory: IMovementCategory;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ movementCategory }) => {
            this.movementCategory = movementCategory;
        });
    }

    previousState() {
        window.history.back();
    }
}
