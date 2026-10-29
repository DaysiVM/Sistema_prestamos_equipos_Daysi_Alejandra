import { useState } from 'react';
import EquiposTabla from './EquiposTabla.jsx';
import PrestamosTabla from './PrestamosTabla.jsx';

export default function Panel({ usuario, onCerrarSesion }) {
  const [pestana, setPestana] = useState('equipos');

  return (
    <div className="app">
      <header className="cabecera">
        <div className="marca">
          <span className="marca__logo" aria-hidden="true">TU</span>
          <div>
            <h1>TecnoUAM</h1>
            <p className="tenue">Préstamo de equipos tecnológicos</p>
          </div>
        </div>
        <div className="cabecera__usuario">
          <span>{usuario.username}</span>
          <span className={`rol rol--${usuario.rol.toLowerCase()}`}>{usuario.rol}</span>
          <button className="boton" onClick={() => onCerrarSesion()}>Cerrar sesión</button>
        </div>
      </header>

      <nav className="pestanas" role="tablist">
        <button role="tab" aria-selected={pestana === 'equipos'}
                className={pestana === 'equipos' ? 'activa' : ''} onClick={() => setPestana('equipos')}>
          Equipos
        </button>
        <button role="tab" aria-selected={pestana === 'prestamos'}
                className={pestana === 'prestamos' ? 'activa' : ''} onClick={() => setPestana('prestamos')}>
          Préstamos
        </button>
      </nav>

      <main className="contenido">
        {pestana === 'equipos'
          ? <EquiposTabla onNoAutorizado={onCerrarSesion} />
          : <PrestamosTabla onNoAutorizado={onCerrarSesion} />}
      </main>
    </div>
  );
}
