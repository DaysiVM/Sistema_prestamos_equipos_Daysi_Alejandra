import { useState } from 'react';
import Login from './components/Login.jsx';
import Panel from './components/Panel.jsx';
import { sesion } from './api/client.js';

export default function App() {
  // Si hay un token guardado, se reanuda la sesión al recargar la página.
  const [usuario, setUsuario] = useState(() => sesion.usuario());
  const [aviso, setAviso] = useState('');

  function alIniciarSesion(datos) {
    sesion.guardar(datos);
    setAviso('');
    setUsuario({ username: datos.username, rol: datos.rol });
  }

  function cerrarSesion(mensaje = '') {
    sesion.cerrar();
    setAviso(mensaje);
    setUsuario(null);
  }

  return usuario
    ? <Panel usuario={usuario} onCerrarSesion={cerrarSesion} />
    : <Login onLogin={alIniciarSesion} aviso={aviso} />;
}
