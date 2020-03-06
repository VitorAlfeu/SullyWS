select u.nome as nome, u.login as usuario, l.data_hora, l.pago, l.valor, ud.nome as nome_devedor, ud.login as usuario_devedor
from lancamentos l
inner join usuarios u  on u.id = l.usuario_id
inner join usuarios ud on ud.id = l.usuario_devedor_id
where u.id = 1
order by u.id; 