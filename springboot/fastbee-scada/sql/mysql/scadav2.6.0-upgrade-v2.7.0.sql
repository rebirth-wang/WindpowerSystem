update
    scada s
    join sys_user su on s.tenant_id = su.user_id
    join sys_dept sd on su.user_id = sd.dept_user_id
    set s.tenant_name = sd.dept_name,
        s.create_by = su.user_name;

update
    scada_component s
    join sys_user su on s.tenant_id = su.user_id
    join sys_dept sd on su.user_id = sd.dept_user_id
    set s.tenant_name = sd.dept_name,
        s.create_by = su.user_name;

update
    scada_echart s
    join sys_user su on s.tenant_id = su.user_id
    join sys_dept sd on su.user_id = sd.dept_user_id
    set s.tenant_name = sd.dept_name,
        s.create_by = su.user_name;

update
    scada_model s
    join sys_user su on s.tenant_id = su.user_id
    join sys_dept sd on su.user_id = sd.dept_user_id
    set s.tenant_name = sd.dept_name,
        s.create_by = su.user_name;

ALTER TABLE `scada_gallery`
    ADD COLUMN `sys_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否系统通用（0-否，1-是）';

update scada_gallery
   set sys_flag = 1
   where tenant_id = 1;

update scada_gallery s
  join sys_dept d on s.tenant_id = d.dept_user_id
  join sys_user su on d.dept_user_id = su.user_id
   set s.tenant_name = d.dept_name,
       s.create_by = su.user_name;