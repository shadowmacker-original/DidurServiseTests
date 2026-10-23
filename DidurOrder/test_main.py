import pytest
import requests
from fastapi.testclient import TestClient
from main import app, PRODUCT_SERVICE_URL

client = TestClient(app)

# 1. Успешное создание заказа
def test_create_order_success(requests_mock):
    requests_mock.get(f"{PRODUCT_SERVICE_URL}/1", json={"id": 1, "name": "Laptop", "price": 1200.50}, status_code=200)
    requests_mock.get(f"{PRODUCT_SERVICE_URL}/2", json={"id": 2, "name": "Smartphone", "price": 800.00}, status_code=200)

    payload = {
        "items": [
            {"product_id": 1, "quantity": 1},
            {"product_id": 2, "quantity": 2}
        ]
    }

    response = client.post("/orders", json=payload)

    assert response.status_code == 201
    data = response.json()
    assert data["total_amount"] == 2800.50
    assert len(data["items"]) == 2

# 2. Ошибка 400 Bad Request при несуществующем product_id
def test_create_order_product_not_found(requests_mock):
    requests_mock.get(f"{PRODUCT_SERVICE_URL}/999", status_code=404)

    payload = {
        "items": [
            {"product_id": 999, "quantity": 1}
        ]
    }

    response = client.post("/orders", json=payload)

    assert response.status_code == 400
    assert "does not exist" in response.json()["detail"]

# 3. Ошибка 503 Service Unavailable, когда "Каталог продуктов" недоступен
def test_create_order_service_unavailable(requests_mock):
    requests_mock.get(f"{PRODUCT_SERVICE_URL}/1", exc=requests.exceptions.ConnectTimeout)

    payload = {
        "items": [
            {"product_id": 1, "quantity": 1}
        ]
    }

    response = client.post("/orders", json=payload)

    assert response.status_code == 503
    assert "Product Catalog service is unavailable" in response.json()["detail"]