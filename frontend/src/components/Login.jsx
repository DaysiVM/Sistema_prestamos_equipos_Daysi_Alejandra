import { useState } from 'react';
import { login, mensajeDeError } from '../api/client.js';

export default function Login({ onLogin, aviso }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');

  async function enviar(evento) {
    evento.preventDefault();
    setCargando(true);
    setError('');
    try {
      // POST /api/auth/login -> { token, username, rol }
      const datos = await login(username.trim(), password);
      onLogin(datos);
    } catch (err) {
      setError(mensajeDeError(err));
    } finally {
      setCargando(false);
    }
  }

  return (
    <main className="login">
      <form className="tarjeta login__form" onSubmit={enviar}>
        <div className="marca">
          <span className="marca__logo" aria-hidden="true">TU</span>
          <div>
            <h1>TecnoUAM</h1>
            <p className="tenue">Préstamo de equipos tecnológicos</p>
          </div>
        </div>

        {aviso && <p className="alerta alerta--aviso">{aviso}</p>}
        {error && <p className="alerta alerta--error" role="alert">{error}</p>}

        <label>
          Usuario
          <input value={username} onChange={(e) => setUsername(e.target.value)}
                 autoComplete="username" required autoFocus />
        </label>
        <label>
          Contraseña
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                 autoComplete="current-password" required />
        </label>

        <button className="boton boton--primario" type="submit" disabled={cargando}>
          {cargando ? 'Ingresando…' : 'Iniciar sesión'}
        </button>
      </form>
    </main>
  );
}
