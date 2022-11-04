SELECT * 
FROM reportpage rp
join history h on h.applDataID=rp.DataModuleID
join closure clo on clo.childID= h.applDictID and clo.level=1
join concept dict on dict.ID=clo.parentID
where h.Go is null 
and h.Come<CURDATE() 
and dict.Identifier='dictionary.host.applications'