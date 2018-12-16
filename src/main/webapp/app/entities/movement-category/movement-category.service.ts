import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IMovementCategory } from 'app/shared/model/movement-category.model';

type EntityResponseType = HttpResponse<IMovementCategory>;
type EntityArrayResponseType = HttpResponse<IMovementCategory[]>;

@Injectable({ providedIn: 'root' })
export class MovementCategoryService {
    public resourceUrl = SERVER_API_URL + 'api/movement-categories';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/movement-categories';

    constructor(private http: HttpClient) {}

    create(movementCategory: IMovementCategory): Observable<EntityResponseType> {
        return this.http.post<IMovementCategory>(this.resourceUrl, movementCategory, { observe: 'response' });
    }

    update(movementCategory: IMovementCategory): Observable<EntityResponseType> {
        return this.http.put<IMovementCategory>(this.resourceUrl, movementCategory, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IMovementCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IMovementCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IMovementCategory[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
