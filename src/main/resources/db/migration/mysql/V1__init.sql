CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `apelido` varchar(255) DEFAULT NULL,
  `data_nasc` datetime NOT NULL,
  `email` varchar(255) NOT NULL,
  `logado` bit(1) NOT NULL DEFAULT b'0',
  `login` varchar(255) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `status` varchar(100) NOT NULL DEFAULT 'AGUARDANDO_CONFIRMACAO_EMAIL',
  `dt_envio_confirm_email` datetime,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `lancamentos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comentario_atualizacao` varchar(255) DEFAULT NULL,
  `data_hora` datetime NOT NULL,
  `dt_hr_a_receber` datetime DEFAULT NULL,
  `dt_hr_atualizacao` datetime DEFAULT NULL,
  `dt_hr_atualizacao_status` datetime DEFAULT NULL,
  `descricao` varchar(255) NOT NULL,
  `status` varchar(60) DEFAULT 'AGUARDANDO_PAGAMENTO',
  `titulo` varchar(255) NOT NULL,
  `valor` double NOT NULL,
  `usuario_id` bigint(20) DEFAULT NULL,
  `usuario_devedor_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn7h1ujyxb06phmlfvci8hi9j0` (`usuario_id`),
  KEY `FKrt8lij38sdq8sab15qk1v5rdm` (`usuario_devedor_id`),
  CONSTRAINT `FKn7h1ujyxb06phmlfvci8hi9j0` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`),
  CONSTRAINT `FKrt8lij38sdq8sab15qk1v5rdm` FOREIGN KEY (`usuario_devedor_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;