import axios from 'axios';

const TOKEN_KEY = 'tecnouam_token';
const USER_KEY = 'tecnouam_usuario';

// Instancia de Axios con la URL del backend (configurable con VITE_API_URL).
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8081',
  timeout: 10000,
});

// Interceptor: agrega "Authorization: Bearer <token>" a cada solicitud protegida.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const sesion = {
  guardar({ token, username, rol }) {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify({ username, rol }));
  },
  usuario() {
    try {
      const datos = localStorage.getItem(USER_KEY);
      return datos && localStorage.getItem(TOKEN_KEY) ? JSON.parse(datos) : null;
    } catch {
      return null;
    }
  },
  cerrar() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};

export async function login(username, password) {
  const { data } = await api.post('/api/auth/login', { username, password });
  return data;
}

export async function obtenerEquipos() {
  const { data } = await api.get('/api/equipos');
  return data;
}

export async function obtenerPrestamos() {
  const { data } = await api.get('/api/prestamos');
  return data;
}

// Traduce un error de Axios en un mensaje entendible para la interfaz.
export function mensajeDeError(error) {
  if (error.response) {
    return error.response.data?.mensaje ?? `Error ${error.response.status}`;
  }
  if (error.request) {
    return 'No se pudo conectar con el servidor. Verifique que el backend esté en ejecución.';
  }
  return error.message;
}

export default api;
