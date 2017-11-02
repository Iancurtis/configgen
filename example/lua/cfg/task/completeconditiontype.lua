local completeconditiontype = {}
completeconditiontype.KillMonster = nil
completeconditiontype.TalkNpc = nil
completeconditiontype.CollectItem = nil

function completeconditiontype:_create(os)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.id = os:ReadInt32() -- 任务完成条件类型（id的范围为1-100）
    o.name = os:ReadString() -- 程序用名字
    return o
end


completeconditiontype.all = {}
function completeconditiontype.get(id)
    return completeconditiontype.all[id]
end

function completeconditiontype._initialize(os, errors)
    for _ = 1, os:ReadSize() do
        local v = completeconditiontype:_create(os)
        if #(v.name) > 0 then
            completeconditiontype[v.name] = v
        end
        completeconditiontype.all[v.id] = v
    end
    if completeconditiontype.KillMonster == nil then
        errors.enumNil("task.completeconditiontype", "KillMonster");
    end
    if completeconditiontype.TalkNpc == nil then
        errors.enumNil("task.completeconditiontype", "TalkNpc");
    end
    if completeconditiontype.CollectItem == nil then
        errors.enumNil("task.completeconditiontype", "CollectItem");
    end
end

return completeconditiontype
