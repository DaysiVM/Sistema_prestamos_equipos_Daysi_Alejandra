import { obtenerPrestamos } from '../api/client.js';
import useDatosApi from '../hooks/useDatosApi.js';
import { Cargando, ErrorCarga, Insignia, ListaVacia } from './EstadoTabla.jsx';

function formatearFecha(fecha) {
  if (!fecha) return '—';
  const [anio, mes, dia] = fecha.split('-');
  return `${dia}/${mes}/${anio}`;
}

export default function PrestamosTabla({ onNoAutorizado }) {
  const { datos: prestamos, cargando, error, recargar } = useDatosApi(obtenerPrestamos, onNoAutorizado);

  return (
    <section>
      <div className="seccion__cabecera">
        <div>
          <h2>Préstamos</h2>
          <p className="tenue">Historial de préstamos y devoluciones</p>
        </div>
        <button className="boton boton--primario" onClick={recargar} disabled={cargando}>
          {cargando ? 'Actualizando…' : 'Actualizar'}
        </button>
      </div>

      {cargando && <Cargando texto="Cargando préstamos…" />}
      {!cargando && error && <ErrorCarga mensaje={error} onReintentar={recargar} />}
      {!cargando && !error && prestamos.length === 0 && (
        <ListaVacia texto="Aún no se ha registrado ningún préstamo." />
      )}

      {!cargando && !error && prestamos.length > 0 && (
        <div className="tabla__contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>#</th>
                <th>Equipo</th>
                <th>Solicitante</th>
                <th>Préstamo</th>
                <th>Devolución prevista</th>
                <th>Devuelto el</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {prestamos.map((p) => (
                <tr key={p.id}>
                  <td className="mono">{p.id}</td>
                  <td>
                    <span className="mono">{p.equipo.codigoPatrimonial}</span>
                    <br /><small className="tenue">{p.equipo.nombre}</small>
                  </td>
                  <td>{p.nombreSolicitante}</td>
                  <td>{formatearFecha(p.fechaPrestamo)}</td>
                  <td>{formatearFecha(p.fechaPrevistaDevolucion)}</td>
                  <td>{formatearFecha(p.fechaDevolucion)}</td>
                  <td><Insignia valor={p.estado} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
