import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IMovement } from 'app/shared/model/movement.model';
import { MovementService } from './movement.service';
import { IMovementCategory } from 'app/shared/model/movement-category.model';
import { MovementCategoryService } from 'app/entities/movement-category';

@Component({
    selector: 'jhi-movement-update',
    templateUrl: './movement-update.component.html'
})
export class MovementUpdateComponent implements OnInit {
    movement: IMovement;
    isSaving: boolean;

    movementcategories: IMovementCategory[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private movementService: MovementService,
        private movementCategoryService: MovementCategoryService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ movement }) => {
            this.movement = movement;
        });
        this.movementCategoryService.query().subscribe(
            (res: HttpResponse<IMovementCategory[]>) => {
                this.movementcategories = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.movement.id !== undefined) {
            this.subscribeToSaveResponse(this.movementService.update(this.movement));
        } else {
            this.subscribeToSaveResponse(this.movementService.create(this.movement));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IMovement>>) {
        result.subscribe((res: HttpResponse<IMovement>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackMovementCategoryById(index: number, item: IMovementCategory) {
        return item.id;
    }
}
