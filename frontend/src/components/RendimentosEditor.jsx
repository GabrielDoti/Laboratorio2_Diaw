export default function RendimentosEditor({ valor, onChange }) {
  const set = (i, campo, v) => onChange(valor.map((r, j) => (j === i ? { ...r, [campo]: v } : r)))
  return (
    <fieldset>
      <legend>Rendimentos (máx. 3)</legend>
      {valor.map((r, i) => (
        <div className="linha" key={i}>
          <input
            placeholder="Entidade empregadora"
            value={r.entidadeEmpregadora}
            onChange={(e) => set(i, 'entidadeEmpregadora', e.target.value)}
            required
          />
          <input
            type="number"
            min="0"
            step="0.01"
            placeholder="Valor (R$)"
            value={r.valor}
            onChange={(e) => set(i, 'valor', e.target.value)}
            required
          />
          <button type="button" className="link" onClick={() => onChange(valor.filter((_, j) => j !== i))}>
            remover
          </button>
        </div>
      ))}
      {valor.length < 3 && (
        <button
          type="button"
          className="secundario"
          onClick={() => onChange([...valor, { entidadeEmpregadora: '', valor: '' }])}
        >
          + Adicionar rendimento
        </button>
      )}
    </fieldset>
  )
}
