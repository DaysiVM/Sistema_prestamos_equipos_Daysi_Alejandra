// Estados de la interfaz compartidos por las tablas: carga, error y lista vacía.

export function Cargando({ texto }) {
  return (
    <div className="estado" role="status">
      <span className="spinner" aria-hidden="true" />
      <p>{texto}</p>
    </div>
  );
}

export function ErrorCarga({ mensaje, onReintentar }) {
  return (
    <div className="estado estado--error" role="alert">
      <p><strong>No se pudo cargar la información.</strong></p>
      <p>{mensaje}</p>
      <button className="boton" onClick={onReintentar}>Reintentar</button>
    </div>
  );
}

export function ListaVacia({ texto }) {
  return (
    <div className="estado">
      <p>{texto}</p>
    </div>
  );
}

export function Insignia({ valor }) {
  return <span className={`insignia insignia--${valor.toLowerCase()}`}>{valor}</span>;
}
