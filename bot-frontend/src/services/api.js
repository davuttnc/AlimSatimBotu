import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    withCredentials: true, // CORS izinleri için gereklidir
});

export const fetchBalance = async () => {
    try {
        const response = await api.get('/api/v1/okx/balance');
        return response.data;
    } catch (error) {
        console.error("Error fetching balance:", error);
        throw error;
    }
};
