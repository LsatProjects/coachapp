import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { IMovementCategory } from 'app/shared/model/movement-category.model';
import { MovementCategoryService } from './movement-category.service';
import { ISport } from 'app/shared/model/sport.model';
import { SportService } from 'app/entities/sport';

@Component({
    selector: 'jhi-movement-category-update',
    templateUrl: './movement-category-update.component.html'
})
export class MovementCategoryUpdateComponent implements OnInit {
    movementCategory: IMovementCategory;
    isSaving: boolean;

    sports: ISport[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private movementCategoryService: MovementCategoryService,
        private sportService: SportService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ movementCategory }) => {
            this.movementCategory = movementCategory;
        });
        this.sportService.query().subscribe(
            (res: HttpResponse<ISport[]>) => {
                this.sports = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.movementCategory.id !== undefined) {
            this.subscribeToSaveResponse(this.movementCategoryService.update(this.movementCategory));
        } else {
            this.subscribeToSaveResponse(this.movementCategoryService.create(this.movementCategory));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IMovementCategory>>) {
        result.subscribe((res: HttpResponse<IMovementCategory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackSportById(index: number, item: ISport) {
        return item.id;
    }
}
