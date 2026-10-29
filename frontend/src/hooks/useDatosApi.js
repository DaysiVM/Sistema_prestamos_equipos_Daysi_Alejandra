import { useCallback, useEffect, useState } from 'react';
import { mensajeDeError } from '../api/client.js';

/**
 * Hook reutilizable: ejecuta una consulta GET a la API y expone los estados
 * de carga, error y datos. `recargar()` vuelve a consultar sin recargar la página.
 * Si la API responde 401 (token ausente/expirado) avisa con onNoAutorizado.
 */
export default function useDatosApi(consulta, onNoAutorizado) {
  const [datos, setDatos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const recargar = useCallback(async () => {
    setCargando(true);
    setError('');
    try {
      setDatos(await consulta());
    } catch (err) {
      if (err.response?.status === 401) {
        onNoAutorizado?.('Su sesión expiró o el token no es válido. Inicie sesión nuevamente.');
        return;
      }
      setError(mensajeDeError(err));
    } finally {
      setCargando(false);
    }
  }, [consulta, onNoAutorizado]);

  // useEffect: consulta la API al montar el componente.
  useEffect(() => {
    recargar();
  }, [recargar]);

  return { datos, cargando, error, recargar };
}
