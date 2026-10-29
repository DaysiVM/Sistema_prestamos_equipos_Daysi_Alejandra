import { obtenerEquipos } from '../api/client.js';
import useDatosApi from '../hooks/useDatosApi.js';
import { Cargando, ErrorCarga, Insignia, ListaVacia } from './EstadoTabla.jsx';

const TIPOS = { PORTATIL: 'Portátil', PROYECTOR: 'Proyector', TABLETA: 'Tableta' };

export default function EquiposTabla({ onNoAutorizado }) {
  // GET /api/equipos (protegido con JWT) mediante Axios + useEffect dentro del hook.
  const { datos: equipos, cargando, error, recargar } = useDatosApi(obtenerEquipos, onNoAutorizado);

  const disponibles = equipos.filter((e) => e.estado === 'DISPONIBLE').length;
  const prestados = equipos.length - disponibles;

  return (
    <section>
      <div className="seccion__cabecera">
        <div>
          <h2>Equipos</h2>
          <p className="tenue">Información en vivo desde la API REST</p>
        </div>
        <button className="boton boton--primario" onClick={recargar} disabled={cargando}>
          {cargando ? 'Actualizando…' : 'Actualizar'}
        </button>
      </div>

      {!cargando && !error && equipos.length > 0 && (
        <div className="resumen">
          <div className="resumen__item"><span>Total</span><strong>{equipos.length}</strong></div>
          <div className="resumen__item resumen__item--ok"><span>Disponibles</span><strong>{disponibles}</strong></div>
          <div className="resumen__item resumen__item--alerta"><span>Prestados</span><strong>{prestados}</strong></div>
        </div>
      )}

      {cargando && <Cargando texto="Cargando equipos…" />}
      {!cargando && error && <ErrorCarga mensaje={error} onReintentar={recargar} />}
      {!cargando && !error && equipos.length === 0 && (
        <ListaVacia texto="No hay equipos registrados todavía." />
      )}

      {!cargando && !error && equipos.length > 0 && (
        <div className="tabla__contenedor">
          <table className="tabla">
            <thead>
              <tr>
                <th>Código</th>
                <th>Nombre</th>
                <th>Tipo</th>
                <th>Estado</th>
              </tr>
            </thead>
            <tbody>
              {equipos.map((equipo) => (
                <tr key={equipo.id}>
                  <td className="mono">{equipo.codigoPatrimonial}</td>
                  <td>{equipo.nombre}</td>
                  <td>{TIPOS[equipo.tipo] ?? equipo.tipo}</td>
                  <td><Insignia valor={equipo.estado} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
