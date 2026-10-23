import os
import requests
from datetime import date, timedelta
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel
from typing import List, Dict

app = FastAPI()

PRODUCT_SERVICE_URL = os.getenv("PRODUCT_SERVICE_URL", "http://localhost:8080/products")

# Хранилище заказов в памяти
orders: Dict[int, dict] = {}
order_id_counter = 0

class OrderItem(BaseModel):
    product_id: int
    quantity: int

class OrderRequest(BaseModel):
    items: List[OrderItem]

class OrderResponse(BaseModel):
    id: int
    total_amount: float
    items: List[OrderItem]
    deliveryDate: str

@app.get("/orders", response_model=List[OrderResponse])
def get_all_orders():
    return list(orders.values())

@app.get("/orders/{order_id}", response_model=OrderResponse)
def get_order_by_id(order_id: int):
    if order_id not in orders:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Order not found"
        )
    return orders[order_id]

@app.post("/orders", response_model=OrderResponse, status_code=status.HTTP_201_CREATED)
def create_order(order_req: OrderRequest):
    total_amount = 0.0

    for item in order_req.items:
        try:
            response = requests.get(f"{PRODUCT_SERVICE_URL}/{item.product_id}", timeout=3.0)
        except requests.exceptions.RequestException:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Product Catalog service is unavailable"
            )

        if response.status_code == 404:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Product with id {item.product_id} does not exist"
            )
        elif response.status_code != 200:
            raise HTTPException(
                status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
                detail="Error response from Product Catalog service"
            )

        product_data = response.json()
        total_amount += product_data["price"] * item.quantity

    global order_id_counter
    order_id_counter += 1
    new_id = order_id_counter

    delivery_date = (date.today() + timedelta(days=5)).isoformat()

    new_order = OrderResponse(
        id=new_id,
        total_amount=round(total_amount, 2),
        items=order_req.items,
        deliveryDate=delivery_date
    )
    orders[new_id] = new_order.dict()
    return new_order